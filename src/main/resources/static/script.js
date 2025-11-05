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
          <button onclick="toggleCompleted(${task.id}, ${task.completed})">
            ${task.completed ? '✅ Yes' : '❌ No'}
          </button>
        </td>
        <td>${new Date(task.createdAt).toLocaleString()}</td>
      </tr>
    `;
    tbody.insertAdjacentHTML('beforeend', row);
  });
}

// Toggle the "completed" status
async function toggleCompleted(id, currentStatus) {
  const updatedTask = { completed: !currentStatus };

  const response = await fetch(`/api/tasks/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(updatedTask)
  });

  if (response.ok) {
    loadTasks(); // Reload table after update
  } else {
    alert('Failed to update task');
  }
}
document.querySelector('#taskForm').addEventListener('submit', async (e) => {
  e.preventDefault();

  const title = document.querySelector('#title').value.trim();
  const description = document.querySelector('#description').value.trim();

  if (!title || !description) {
    alert('Please enter both title and description!');
    return;
  }

  const newTask = { title, description };

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

// Run loadTasks when the page loads
document.addEventListener('DOMContentLoaded', loadTasks);
