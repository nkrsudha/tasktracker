let users = [];
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

async function updateStatus(id, status) {
  const response = await fetch(`/api/tasks/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ status })
  });

  if (response.ok) {
    loadTasks();
  } else {
    alert("❌ Failed to update status.");
  }
}


// Load all users and display them
async function loadUsers() {
  const response = await fetch('/api/users');
  users = await response.json();

  const tbody = document.querySelector('#userTable tbody');
  tbody.innerHTML = '';

  users.forEach(user => {
    const row = `
      <tr>
        <td>${user.id}</td>
        <td><a href="#" class="user-link user-popup" data-username="${escapeHtml(user.username)}">${escapeHtml(user.username)}</a></td>
        <td>${user.email}</td>
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

  /** Update User Dropdown */
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

// Handle user registration form submission
document.querySelector('#userForm').addEventListener('submit', async (e) => {
  e.preventDefault();

  const user = {
    username: document.querySelector('#username').value,
    email: document.querySelector('#email').value,
    password: document.querySelector('#password').value
  };

  const response = await fetch('/api/users', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(user)
  });

  if (response.ok) {
    alert('✅ User registered successfully!');
    document.querySelector('#userForm').reset();
    loadUsers();
  } else {
    alert('❌ Failed to register user.');
  }
});

// Delete user by ID
async function deleteUser(id) {
  if (!confirm('Are you sure you want to delete this user?')) return;

  const response = await fetch(`/api/users/${id}`, {
    method: 'DELETE'
  });

  if (response.ok) {
    alert('✅ User deleted successfully!');
    loadUsers();
  } else {
    alert('❌ Failed to delete user.');
  }
}

// Load users when the page loads
document.addEventListener('DOMContentLoaded', () => {
  showSection('userSection');
  loadUsers();
});
// Load all tasks and display them in the table
async function loadTasks() {
  const response = await fetch('/api/tasks');
  const tasks = await response.json();

  const tbody = document.querySelector('#taskTable tbody');
  tbody.innerHTML = '';

  tasks.forEach(task => {
    const row = `
      <tr>
        <td>${task.id}</td>
        <td>${task.title}</td>
        <td>${task.description}</td>
        <td>
    <select class="task-assign-select" data-task-id="${task.id}">
        <option value="">Unassigned</option>
        ${users.map(user => `
            <option value="${user.id}"
                ${task.assignedUser && Number(task.assignedUser.id) === Number(user.id) ? "selected" : ""}>
                ${user.username}
            </option>
        `).join("")}
    </select>
</td>

        <td>
          <select onchange="updateStatus(${task.id}, this.value)">
            ${statusOptions(task.status)}
          </select>
        </td>

        <td>${new Date(task.createdAt).toLocaleString()}</td>
        <td>
          <button onclick="deleteTask(${task.id})"
            style="background-color:#e74c3c;color:white;border:none;border-radius:4px;cursor:pointer;">
            🗑️ Delete
          </button>
        </td>
      </tr>
    `;
    tbody.insertAdjacentHTML('beforeend', row);
  });
}

// Delete task
async function deleteTask(id) {
  if (!confirm('Are you sure you want to delete this task?')) return;

  const response = await fetch(`/api/tasks/${id}`, {
    method: 'DELETE'
  });

  if (response.ok) {
    loadTasks();
  } else {
    alert('❌ Failed to delete task.');
  }
}

// Add new task
document.querySelector('#taskForm').addEventListener('submit', async (e) => {
  e.preventDefault();

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
    body: JSON.stringify(newTask)
  });

  if (response.ok) {
    document.querySelector('#taskForm').reset();
    loadTasks();
  } else {
    alert('❌ Failed to add task.');
  }
});

// Load tasks when page loads

document.addEventListener("change", function (event) {
    if (event.target.classList.contains("task-assign-select")) {

        const taskId = event.target.getAttribute("data-task-id");
        const userId = event.target.value;

        fetch(`/api/tasks/${taskId}/assign`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ userId: userId === "" ? null : userId })
        })
        .then(res => res.json())
        .then(data => {
            alert("User updated successfully!");
            loadTasks(); // Refresh table
        })
        .catch(err => console.error(err));
    }
});

function escapeHtml(str) {
  return String(str)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

document.addEventListener("DOMContentLoaded", () => {

  // Popup elements
  const userTasksModal = document.getElementById("userTasksModal");
  const closeUserTasksModal = document.getElementById("closeUserTasksModal");
  const userTasksTitle = document.getElementById("userTasksTitle");
  const userTasksMsg = document.getElementById("userTasksMsg");
  const userTasksTable = document.getElementById("userTasksTable");
  const userTasksBody = document.getElementById("userTasksBody");

  // Safety check (prevents crash)
  if (!userTasksModal || !closeUserTasksModal || !userTasksTitle || !userTasksMsg || !userTasksTable || !userTasksBody) {
    console.error("Popup modal elements not found. Check IDs in HTML.");
    return;
  }

  // Close popup
  closeUserTasksModal.addEventListener("click", () => {
    userTasksModal.style.display = "none";
  });

  // Close when clicking outside the box
  userTasksModal.addEventListener("click", (e) => {
    if (e.target === userTasksModal) userTasksModal.style.display = "none";
  });

  // Click username -> open popup and load tasks
  document.addEventListener("click", async (e) => {
    const link = e.target.closest(".user-popup");
    if (!link) return;

    e.preventDefault();

    const username = link.dataset.username;

    // Reset popup UI
    userTasksTitle.textContent = `Tasks for ${username}`;
    userTasksMsg.textContent = "Loading...";
    userTasksMsg.style.display = "block";
    userTasksTable.style.display = "none";
    userTasksBody.innerHTML = "";

    // Show popup
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
