import { createContext, useContext, useMemo, useState, type ReactNode } from "react";

type AuthContextType = {
  token: string | null;
  userName: string | null;
  login: (token: string, userName?: string | null) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType | null>(null);

type AuthProviderProps = {
  children: ReactNode;
};

const TOKEN_KEY = "token";
const USERNAME_KEY = "username";

export const AuthProvider = ({ children }: AuthProviderProps) => {
  const [token, setToken] = useState(localStorage.getItem(TOKEN_KEY));
  const [userName, setUserName] = useState(localStorage.getItem(USERNAME_KEY));

  const login = (nextToken: string, nextUserName?: string | null) => {
    localStorage.setItem(TOKEN_KEY, nextToken);
    setToken(nextToken);

    if (nextUserName) {
      localStorage.setItem(USERNAME_KEY, nextUserName);
      setUserName(nextUserName);
    }
  };

  const logout = () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USERNAME_KEY);
    setToken(null);
    setUserName(null);
  };

  const value = useMemo(
    () => ({
      token,
      userName,
      login,
      logout,
    }),
    [token, userName]
  );

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return context;
};