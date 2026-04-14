import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";

function parseAuthResponse(data: unknown): { token: string; userName: string | null } {
  const payload = (data ?? {}) as Record<string, unknown>;
  const token = String(payload.token ?? payload.accessToken ?? "");
  const userName = String(
    payload.name ?? (payload.user as Record<string, unknown> | undefined)?.name ?? ""
  );
  return { token, userName: userName || null };
}

export default function Register() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleRegister = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError("");

    if (name.trim().length < 2) {
      setError("Name must be at least 2 characters.");
      return;
    }
    if (!email.includes("@")) {
      setError("Please enter a valid email.");
      return;
    }
    if (password.length < 6) {
      setError("Password must be at least 6 characters.");
      return;
    }

    try {
      setLoading(true);
      const response = await api.post("/auth/register", { name, email, password });
      const { token, userName } = parseAuthResponse(response.data);

      if (token) {
        login(token, userName ?? name);
        navigate("/");
        return;
      }

      const loginResponse = await api.post("/auth/login", { email, password });
      const loginData = parseAuthResponse(loginResponse.data);
      if (!loginData.token) {
        throw new Error("Unable to authenticate after register.");
      }
      login(loginData.token, loginData.userName ?? name);
      navigate("/");
    } catch (err) {
      setError("Registration failed. Please try again.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="auth-page">
      <form className="auth-card" onSubmit={handleRegister}>
        <h1>Create account</h1>
        <p className="muted">Start organizing projects with Taskflow.</p>

        <label className="field">
          <span>Name</span>
          <input
            value={name}
            onChange={(e) => setName(e.target.value)}
            type="text"
            placeholder="Your name"
            required
          />
        </label>

        <label className="field">
          <span>Email</span>
          <input
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            type="email"
            placeholder="you@example.com"
            required
          />
        </label>

        <label className="field">
          <span>Password</span>
          <input
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            type="password"
            placeholder="Minimum 6 characters"
            required
          />
        </label>

        {error && <p className="error">{error}</p>}

        <button type="submit" disabled={loading}>
          {loading ? "Creating account..." : "Register"}
        </button>

        <p className="muted">
          Already have an account? <Link to="/login">Sign in</Link>
        </p>
      </form>
    </main>
  );
}
