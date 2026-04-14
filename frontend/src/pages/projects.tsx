import { useEffect, useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import type { Project } from "../types";

function getProjectsPayload(data: unknown): Project[] {
  if (Array.isArray(data)) {
    return data as Project[];
  }
  if (data && typeof data === "object" && Array.isArray((data as { projects?: unknown[] }).projects)) {
    return (data as { projects: Project[] }).projects;
  }
  return [];
}

export default function Projects() {
  const navigate = useNavigate();
  const { userName, logout } = useAuth();
  const [projects, setProjects] = useState<Project[]>([]);
  const [name, setName] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const fetchProjects = async () => {
    try {
      setLoading(true);
      setError("");
      const res = await api.get("/projects");
      setProjects(getProjectsPayload(res.data));
    } catch (err) {
      setError("Could not load projects.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProjects();
  }, []);

  const createProject = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!name.trim()) {
      setError("Project name is required.");
      return;
    }

    try {
      setSaving(true);
      setError("");
      await api.post("/projects", { name: name.trim() });
      setName("");
      await fetchProjects();
    } catch (err) {
      setError("Could not create project.");
      console.error(err);
    } finally {
      setSaving(false);
    }
  };

  return (
    <main className="page-shell">
      <nav className="navbar">
        <div>
          <h2>Taskflow</h2>
          <p className="muted">Welcome, {userName ?? "User"}</p>
        </div>
        <button type="button" onClick={logout} className="secondary-btn">
          Logout
        </button>
      </nav>

      <section className="card">
        <h3>Projects</h3>
        <form className="inline-form" onSubmit={createProject}>
          <input
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="New project name"
            aria-label="Project name"
          />
          <button type="submit" disabled={saving}>
            {saving ? "Creating..." : "Create project"}
          </button>
        </form>
        {error && <p className="error">{error}</p>}
      </section>

      <section className="card">
        {loading && <p className="muted">Loading projects...</p>}
        {!loading && projects.length === 0 && (
          <p className="muted">No projects yet. Create your first project to get started.</p>
        )}

        <div className="project-grid">
          {projects.map((project) => (
            <button
              type="button"
              key={project.id}
              className="project-card"
              onClick={() => navigate(`/projects/${project.id}`)}
            >
              <h4>{project.name}</h4>
              <p className="muted">{project.description || "No description yet."}</p>
            </button>
          ))}
        </div>
      </section>
    </main>
  );
}