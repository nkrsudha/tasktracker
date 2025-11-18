// Load all users and display them
async function loadUsers() {
  const response = await fetch('/users');
  const users = await response.json();

  const tbody = document.querySelector('#userTable tbody');
  tbody.innerHTML = '';

  users.forEach(user => {
    const row = `
      <tr>
        <td>${user.id}</td>
        <td>${user.username}</td>
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
}

// Handle user registration form submission
document.querySelector('#userForm').addEventListener('submit', async (e) => {
  e.preventDefault();

  const user = {
    username: document.querySelector('#username').value,
    email: document.querySelector('#email').value,
    password: document.querySelector('#password').value
  };

  const response = await fetch('/users', {
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

  const response = await fetch(`/users/${id}`, {
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
document.addEventListener('DOMContentLoaded', loadUsers);

// Load all tasks and display them in the table
async function loadTasks() {
  const response = await fetch('/tasks');
  const tasks = await response.json();

  const tbody = document.querySelector('#taskTable tbody');
  tbody.innerHTML = '';

  tasks.forEach(task => {
    const row = `
      <tr>
        <td>${task.id}</td>
        <td>${task.title}</td>
        <td>${task.description}</td>
        <td>${task.user ? task.user.username : "-"}</td>
        <td>
          <button onclick="toggleCompleted(${task.id}, ${task.completed})">
            ${task.completed ? '✅ Yes' : '❌ No'}
          </button>
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

// Toggle completed
async function toggleCompleted(id, currentStatus) {
  const updatedTask = { completed: !currentStatus };

  const response = await fetch(`/tasks/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(updatedTask)
  });

  if (response.ok) {
    loadTasks();
  } else {
    alert('❌ Failed to update task.');
  }
}

// Delete task
async function deleteTask(id) {
  if (!confirm('Are you sure you want to delete this task?')) return;

  const response = await fetch(`/tasks/${id}`, {
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

  const response = await fetch('/tasks', {
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
document.addEventListener('DOMContentLoaded', loadTasks);
