let users = [];
let CURRENT_ROLE = null; 

function showSection(sectionId) {
  document.getElementById('userSection').style.display = "none";
  document.getElementById('taskSection').style.display = "none";
  document.getElementById(sectionId).style.display = "block";
}

function statusLabel(v) {
  if (!v) return "";
  return v
    .toLowerCase()
    .replace(/_/g, " ")
    .replace(/\b\w/g, c => c.toUpperCase());
}

function statusOptions(selected) {
  const values = ["TO_DO", "IN_PROGRESS", "COMPLETED"];
  return values
    .map(v => `<option value="${v}" ${v === selected ? "selected" : ""}>${statusLabel(v)}</option>`)
    .join("");
}

async function fetchRole() {
  const res = await fetch("/api/me", { credentials: "same-origin" });
  if (!res.ok) return null;

  const me = await res.json();
  const auth = JSON.stringify(me.authorities || []);
  if (auth.includes("ROLE_ADMIN")) return "ROLE_ADMIN";
  if (auth.includes("ROLE_VIEWER")) return "ROLE_VIEWER";
  if (auth.includes("ROLE_USER")) return "ROLE_USER";
  return null;
}

function applyRoleUI() {
  const isAdmin = CURRENT_ROLE === "ROLE_ADMIN";
  const isViewer = CURRENT_ROLE === "ROLE_VIEWER";

  // Hide Users icon for non-admin
  const userIcon = document.getElementById("usersBtn"); // change to usersBtn if you used that id
  if (userIcon && !isAdmin) userIcon.style.display = "none";

  // Viewer: hide add task + delete column, disable assign/status
  if (isViewer) {
    const addBtn = document.getElementById("addTaskBtn");
    if (addBtn) addBtn.style.display = "none";

    const assignSel = document.getElementById("taskUser");
    if (assignSel) assignSel.disabled = true;

    const deleteHeader = document.getElementById("deleteHeader");
    if (deleteHeader) deleteHeader.style.display = "none";
  }
}

async function updateStatus(id, status) {
  // VIEWER cannot update
  if (CURRENT_ROLE === "ROLE_VIEWER") {
    alert("❌ Viewer cannot update status.");
    return;
  }

  const response = await fetch(`/api/tasks/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ status })
  });

  if (response.ok) {
    loadTasks();
  } else if (response.status === 403) {
    alert("❌ Access denied.");
  } else {
    alert("❌ Failed to update status.");
  }
}

// USERS 

async function loadUsers() {
  const response = await fetch('/api/users', { credentials: "same-origin" });

  if (response.status === 403) {
    alert("Access denied: Admin only.");
    showSection("taskSection");
    return;
  }

  users = await response.json();

  const tbody = document.querySelector('#userTable tbody');
  tbody.innerHTML = '';

  users.forEach(user => {
    const row = `
      <tr>
        <td>${user.id}</td>
        <td><a href="#" class="user-link user-popup" data-username="${escapeHtml(user.username)}">${escapeHtml(user.username)}</a></td>
        <td>${escapeHtml(user.email || "")}</td>
        <td>
          <button onclick="deleteUser(${user.id})"
            style="background-color:#e74c3c;color:white;border:none;border-radius:4px;cursor:pointer;">
            🗑️ Delete
          </button>
        </td>
      </tr>
    `;
    tbody.insertAdjacentHTML('beforeend', row);
  });

  // Update User Dropdown (for task assignment) - ADMIN only page normally
  const userSelect = document.getElementById('taskUser');
  userSelect.innerHTML = '<option value="">Assign to User</option>';

  users.forEach(user => {
    const option = document.createElement('option');
    option.value = user.id;
    option.textContent = user.username;
    userSelect.appendChild(option);
  });

  loadTasks();
}

document.querySelector('#userForm').addEventListener('submit', async (e) => {
  e.preventDefault();

  if (CURRENT_ROLE !== "ROLE_ADMIN") {
    alert("Access denied: Admin only.");
    return;
  }

  const user = {
    username: document.querySelector('#username').value,
    email: document.querySelector('#email').value,
    password: document.querySelector('#password').value
  };

  const response = await fetch('/api/users', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(user),
    credentials: "same-origin"
  });

  if (response.ok) {
    alert('✅ User registered successfully!');
    document.querySelector('#userForm').reset();
    loadUsers();
  } else if (response.status === 403) {
    alert("Access denied: Admin only.");
  } else {
    alert('❌ Failed to register user.');
  }
});

async function deleteUser(id) {
  if (CURRENT_ROLE !== "ROLE_ADMIN") {
    alert("Access denied: Admin only.");
    return;
  }

  if (!confirm('Are you sure you want to delete this user?')) return;

  const response = await fetch(`/api/users/${id}`, {
    method: 'DELETE',
    credentials: "same-origin"
  });

  if (response.ok) {
    alert('✅ User deleted successfully!');
    loadUsers();
  } else if (response.status === 403) {
    alert("Access denied: Admin only.");
  } else {
    alert('❌ Failed to delete user.');
  }
}

//  TASKS 

async function loadTasks() {
  const response = await fetch('/api/tasks', { credentials: "same-origin" });

  if (!response.ok) {
    if (response.status === 403) alert("❌ Access denied.");
    else alert("❌ Failed to load tasks.");
    return;
  }

  const tasks = await response.json();
  const tbody = document.querySelector('#taskTable tbody');
  tbody.innerHTML = '';

  const isAdmin = CURRENT_ROLE === "ROLE_ADMIN";
  const isViewer = CURRENT_ROLE === "ROLE_VIEWER";

  tasks.forEach(task => {
    // Assigned user cell:
    let assignedCell = "";
    if (isAdmin) {
      assignedCell = `
        <select class="task-assign-select" data-task-id="${task.id}">
          <option value="">Unassigned</option>
          ${users.map(user => `
            <option value="${user.id}"
              ${task.assignedUser && Number(task.assignedUser.id) === Number(user.id) ? "selected" : ""}>
              ${escapeHtml(user.username)}
            </option>
          `).join("")}
        </select>
      `;
    } else {
      assignedCell = escapeHtml(task.assignedUser ? task.assignedUser.username : "Unassigned");
    }

    // Status cell:
    const statusCell = isViewer
      ? `<select disabled>${statusOptions(task.status)}</select>`
      : `<select onchange="updateStatus(${task.id}, this.value)">${statusOptions(task.status)}</select>`;

    // Delete cell:
    const deleteCell = isViewer
      ? `` // no delete for viewer
      : `
        <button class="delete-btn" onclick="deleteTask(${task.id})"
          style="background-color:#e74c3c;color:white;border:none;border-radius:4px;cursor:pointer;">
          🗑️ Delete
        </button>
      `;

    const row = `
      <tr>
        <td>${task.id}</td>
        <td>${escapeHtml(task.title || "")}</td>
        <td>${escapeHtml(task.description || "")}</td>
        <td>${assignedCell}</td>
        <td>${statusCell}</td>
        <td>${new Date(task.createdAt).toLocaleString()}</td>
        ${isViewer ? "" : `<td>${deleteCell}</td>`}
      </tr>
    `;
    tbody.insertAdjacentHTML('beforeend', row);
  });

  // Viewer: hide delete header + last column if table is built without it
  if (isViewer) {
    const deleteHeader = document.getElementById("deleteHeader");
    if (deleteHeader) deleteHeader.style.display = "none";
  }
}

async function deleteTask(id) {
  if (CURRENT_ROLE === "ROLE_VIEWER") {
    alert("❌ Viewer cannot delete tasks.");
    return;
  }

  if (!confirm('Are you sure you want to delete this task?')) return;

  const response = await fetch(`/api/tasks/${id}`, {
    method: 'DELETE',
    credentials: "same-origin"
  });

  if (response.ok) {
    loadTasks();
  } else if (response.status === 403) {
    alert("❌ Access denied.");
  } else {
    alert('❌ Failed to delete task.');
  }
}

document.querySelector('#taskForm').addEventListener('submit', async (e) => {
  e.preventDefault();

  if (CURRENT_ROLE === "ROLE_VIEWER") {
    alert("❌ Viewer cannot create tasks.");
    return;
  }

  const title = document.querySelector('#title').value.trim();
  const description = document.querySelector('#description').value.trim();
  const userId = document.getElementById('taskUser').value;

  if (!title || !description) {
    alert('⚠️ Please enter both title and description!');
    return;
  }

  if (!userId) {
    alert('⚠️ Please assign the task to a user!');
    return;
  }

  const newTask = { title, description, userId };

  const response = await fetch('/api/tasks', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(newTask),
    credentials: "same-origin"
  });

  if (response.ok) {
    document.querySelector('#taskForm').reset();
    loadTasks();
  } else if (response.status === 403) {
    alert("❌ Access denied.");
  } else {
    alert('❌ Failed to add task.');
  }
});

// Assignment change (ADMIN only)
document.addEventListener("change", function (event) {
  if (!event.target.classList.contains("task-assign-select")) return;

  if (CURRENT_ROLE !== "ROLE_ADMIN") {
    alert("❌ Only admin can assign tasks.");
    return;
  }

  const taskId = event.target.getAttribute("data-task-id");
  const userId = event.target.value;

  fetch(`/api/tasks/${taskId}/assign`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ userId: userId === "" ? null : userId }),
    credentials: "same-origin"
  })
    .then(res => {
      if (res.status === 403) throw new Error("403");
      return res.json();
    })
    .then(() => {
      alert("User updated successfully!");
      loadTasks();
    })
    .catch(err => {
      if (String(err).includes("403")) alert("❌ Access denied.");
      else console.error(err);
    });
});

function escapeHtml(str) {
  return String(str)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

// INIT + POPUP 

document.addEventListener('DOMContentLoaded', async () => {
  // 1) detect role
  CURRENT_ROLE = await fetchRole();

  // 2) apply UI hiding
  applyRoleUI();
  
  // Always open Tasks page after login
 showSection('taskSection');
 loadTasks();

// Admin can still load users in background
if (CURRENT_ROLE === "ROLE_ADMIN") {
  loadUsers();
}


  // Popup setup (your existing code) 
  const userTasksModal = document.getElementById("userTasksModal");
  const closeUserTasksModal = document.getElementById("closeUserTasksModal");
  const userTasksTitle = document.getElementById("userTasksTitle");
  const userTasksMsg = document.getElementById("userTasksMsg");
  const userTasksTable = document.getElementById("userTasksTable");
  const userTasksBody = document.getElementById("userTasksBody");

  if (!userTasksModal || !closeUserTasksModal || !userTasksTitle || !userTasksMsg || !userTasksTable || !userTasksBody) {
    console.error("Popup modal elements not found. Check IDs in HTML.");
    return;
  }

  closeUserTasksModal.addEventListener("click", () => {
    userTasksModal.style.display = "none";
  });

  userTasksModal.addEventListener("click", (e) => {
    if (e.target === userTasksModal) userTasksModal.style.display = "none";
  });

  document.addEventListener("click", async (e) => {
    const link = e.target.closest(".user-popup");
    if (!link) return;

    e.preventDefault();
    const username = link.dataset.username;

    userTasksTitle.textContent = `Tasks for ${username}`;
    userTasksMsg.textContent = "Loading...";
    userTasksMsg.style.display = "block";
    userTasksTable.style.display = "none";
    userTasksBody.innerHTML = "";
    userTasksModal.style.display = "block";

    try {
      const res = await fetch(`/api/tasks/by-user/${encodeURIComponent(username)}`, {
        credentials: "same-origin"
      });
      if (!res.ok) throw new Error("HTTP " + res.status);

      const tasks = await res.json();

      if (!tasks || tasks.length === 0) {
        userTasksMsg.textContent = "No tasks for this user.";
        return;
      }

      userTasksMsg.style.display = "none";
      userTasksTable.style.display = "table";

      tasks.forEach(t => {
        const tr = document.createElement("tr");
        tr.innerHTML = `<td>${escapeHtml(t.title)}</td><td>${escapeHtml(statusLabel(t.status))}</td>`;
        userTasksBody.appendChild(tr);
      });

    } catch (err) {
      console.error(err);
      userTasksMsg.textContent = "Failed to load tasks.";
    }
  });
});
