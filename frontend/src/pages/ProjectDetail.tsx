import { useEffect, useMemo, useState, type FormEvent } from "react";
import { Link, useParams } from "react-router-dom";
import api from "../api/axios";
import type { Task } from "../types";

type TaskFormState = {
  title: string;
  status: string;
  priority: string;
  assigneeId: string;
  dueDate: string;
};

const INITIAL_FORM: TaskFormState = {
  title: "",
  status: "todo",
  priority: "medium",
  assigneeId: "",
  dueDate: "",
};

function normalizeTasks(data: unknown): Task[] {
  if (Array.isArray(data)) {
    return data as Task[];
  }
  if (data && typeof data === "object" && Array.isArray((data as { tasks?: unknown[] }).tasks)) {
    return (data as { tasks: Task[] }).tasks;
  }
  return [];
}

export default function ProjectDetail() {
  const { id } = useParams();
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);
  const [statusFilter, setStatusFilter] = useState("all");
  const [assigneeFilter, setAssigneeFilter] = useState("all");
  const [isPanelOpen, setIsPanelOpen] = useState(false);
  const [editingTaskId, setEditingTaskId] = useState<string | null>(null);
  const [form, setForm] = useState<TaskFormState>(INITIAL_FORM);

  const fetchTasks = async () => {
    if (!id) {
      setError("Project id is missing.");
      return;
    }
    try {
      setLoading(true);
      setError("");
      const response = await api.get(`/projects/${id}/tasks`);
      setTasks(normalizeTasks(response.data));
    } catch (err) {
      setError("Failed to load tasks.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTasks();
  }, [id]);

  const assignees = useMemo(() => {
    const values = tasks
      .map((task) => task.assigneeId)
      .filter((value): value is string => Boolean(value));
    return Array.from(new Set(values));
  }, [tasks]);

  const filteredTasks = useMemo(
    () =>
      tasks.filter((task) => {
        const byStatus = statusFilter === "all" || task.status === statusFilter;
        const byAssignee = assigneeFilter === "all" || (task.assigneeId ?? "unassigned") === assigneeFilter;
        return byStatus && byAssignee;
      }),
    [tasks, statusFilter, assigneeFilter]
  );

  const openCreatePanel = () => {
    setEditingTaskId(null);
    setForm(INITIAL_FORM);
    setIsPanelOpen(true);
  };

  const openEditPanel = (task: Task) => {
    setEditingTaskId(task.id);
    setForm({
      title: task.title,
      status: task.status || "todo",
      priority: task.priority || "medium",
      assigneeId: task.assigneeId || "",
      dueDate: task.dueDate ? task.dueDate.slice(0, 10) : "",
    });
    setIsPanelOpen(true);
  };

  const saveTask = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!id) {
      return;
    }
    if (!form.title.trim()) {
      setError("Task title is required.");
      return;
    }

    try {
      setSaving(true);
      setError("");
      const payload = {
        title: form.title.trim(),
        status: form.status,
        priority: form.priority,
        assigneeId: form.assigneeId || null,
        dueDate: form.dueDate || undefined,
      };

      if (editingTaskId) {
        await api.put(`/projects/${id}/tasks/${editingTaskId}`, payload);
      } else {
        await api.post(`/projects/${id}/tasks`, payload);
      }

      setIsPanelOpen(false);
      setForm(INITIAL_FORM);
      await fetchTasks();
    } catch (err) {
      setError("Failed to save task.");
      console.error(err);
    } finally {
      setSaving(false);
    }
  };

  const updateStatusOptimistic = async (task: Task, nextStatus: string) => {
    if (!id || task.status === nextStatus) {
      return;
    }

    const snapshot = tasks;
    setTasks((prev) => prev.map((item) => (item.id === task.id ? { ...item, status: nextStatus } : item)));

    try {
      await api.patch(`/projects/${id}/tasks/${task.id}`, { status: nextStatus });
    } catch (err) {
      try {
        await api.put(`/projects/${id}/tasks/${task.id}`, {
          title: task.title,
          description: task.description,
          status: nextStatus,
          priority: task.priority,
          assigneeId: task.assigneeId ?? null,
          dueDate: task.dueDate ?? null,
        });
      } catch (fallbackErr) {
        setTasks(snapshot);
        setError("Failed to update status. Restored previous value.");
        console.error(fallbackErr);
      }
    }
  };

  return (
    <main className="page-shell">
      <nav className="navbar">
        <div>
          <h2>Project tasks</h2>
          <p className="muted">Manage statuses, assignees, and due dates.</p>
        </div>
        <div className="inline-actions">
          <Link className="secondary-btn" to="/">
            Back to projects
          </Link>
          <button type="button" onClick={openCreatePanel}>
            + New task
          </button>
        </div>
      </nav>

      <section className="card filters">
        <label className="field">
          <span>Status</span>
          <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
            <option value="all">All</option>
            <option value="todo">To do</option>
            <option value="in_progress">In progress</option>
            <option value="done">Done</option>
          </select>
        </label>
        <label className="field">
          <span>Assignee</span>
          <select value={assigneeFilter} onChange={(e) => setAssigneeFilter(e.target.value)}>
            <option value="all">All</option>
            <option value="unassigned">Unassigned</option>
            {assignees.map((assignee) => (
              <option key={assignee} value={assignee}>
                {assignee}
              </option>
            ))}
          </select>
        </label>
      </section>

      {error && <p className="error">{error}</p>}

      <section className="card">
        {loading && <p className="muted">Loading tasks...</p>}
        {!loading && filteredTasks.length === 0 && (
          <p className="muted">No tasks found for the selected filters.</p>
        )}
        <div className="task-list">
          {filteredTasks.map((task) => (
            <article key={task.id} className="task-item">
              <div>
                <h4>{task.title}</h4>
                <p className="muted">
                  Priority: {task.priority || "medium"} | Assignee: {task.assigneeId || "unassigned"}
                </p>
              </div>
              <div className="inline-actions">
                <select value={task.status} onChange={(e) => updateStatusOptimistic(task, e.target.value)}>
                  <option value="todo">To do</option>
                  <option value="in_progress">In progress</option>
                  <option value="done">Done</option>
                </select>
                <button type="button" className="secondary-btn" onClick={() => openEditPanel(task)}>
                  Edit
                </button>
              </div>
            </article>
          ))}
        </div>
      </section>

      {isPanelOpen && (
        <aside className="side-panel">
          <form className="card" onSubmit={saveTask}>
            <h3>{editingTaskId ? "Edit task" : "Create task"}</h3>
            <label className="field">
              <span>Title</span>
              <input
                value={form.title}
                onChange={(e) => setForm((prev) => ({ ...prev, title: e.target.value }))}
                required
              />
            </label>
            <label className="field">
              <span>Status</span>
              <select
                value={form.status}
                onChange={(e) => setForm((prev) => ({ ...prev, status: e.target.value }))}
              >
                <option value="todo">To do</option>
                <option value="in_progress">In progress</option>
                <option value="done">Done</option>
              </select>
            </label>
            <label className="field">
              <span>Priority</span>
              <select
                value={form.priority}
                onChange={(e) => setForm((prev) => ({ ...prev, priority: e.target.value }))}
              >
                <option value="low">Low</option>
                <option value="medium">Medium</option>
                <option value="high">High</option>
              </select>
            </label>
            <label className="field">
              <span>Assignee ID</span>
              <input
                value={form.assigneeId}
                onChange={(e) => setForm((prev) => ({ ...prev, assigneeId: e.target.value }))}
                placeholder="UUID (optional)"
              />
            </label>
            <label className="field">
              <span>Due date</span>
              <input
                type="date"
                value={form.dueDate}
                onChange={(e) => setForm((prev) => ({ ...prev, dueDate: e.target.value }))}
              />
            </label>
            <div className="inline-actions">
              <button type="button" className="secondary-btn" onClick={() => setIsPanelOpen(false)}>
                Cancel
              </button>
              <button type="submit" disabled={saving}>
                {saving ? "Saving..." : "Save task"}
              </button>
            </div>
          </form>
        </aside>
      )}
    </main>
  );
}