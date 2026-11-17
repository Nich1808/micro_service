import { Navigate, Route, Routes, useLocation } from 'react-router-dom';
import { Layout } from './components/Layout';
import { isAuthenticated } from './lib/oidc';
import { ActivityPage } from './pages/Activity';
import { CallbackPage } from './pages/Callback';
import { DeactivatePage } from './pages/Deactivate';
import { LinkedAccountsPage } from './pages/LinkedAccounts';
import { LoginHistoryPage } from './pages/LoginHistory';
import { LoginPage } from './pages/Login';
import { ProfilePage } from './pages/Profile';
import { SecurityPage } from './pages/Security';
import { SessionsPage } from './pages/Sessions';
import { AdminClientsPage } from './pages/admin/Clients';
import { AuditLogsPage } from './pages/admin/AuditLogs';
import { AdminOverviewPage } from './pages/admin/Overview';
import { AdminPermissionsPage } from './pages/admin/Permissions';
import { AdminRolesPage } from './pages/admin/Roles';
import { SettingsPage } from './pages/admin/Settings';
import { AdminUsersPage } from './pages/admin/Users';

function RequireAuth({ children }: { children: React.ReactNode }) {
  const location = useLocation();

  if (!isAuthenticated()) {
    return <Navigate to="/" replace state={{ from: location.pathname }} />;
  }

  return <>{children}</>;
}

export function App() {
  return (
    <Routes>
      <Route
        path="/"
        element={isAuthenticated() ? <Navigate to="/profile" replace /> : <LoginPage />}
      />
      <Route path="/callback" element={<CallbackPage />} />

      <Route
        element={
          <RequireAuth>
            <Layout />
          </RequireAuth>
        }
      >
        <Route path="/profile" element={<ProfilePage />} />
        <Route path="/security" element={<SecurityPage />} />
        <Route path="/accounts" element={<LinkedAccountsPage />} />
        <Route path="/sessions" element={<SessionsPage />} />
        <Route path="/login-history" element={<LoginHistoryPage />} />
        <Route path="/activity" element={<ActivityPage />} />
        <Route path="/deactivate" element={<DeactivatePage />} />
        <Route path="/admin/overview" element={<AdminOverviewPage />} />
        <Route path="/admin/users" element={<AdminUsersPage />} />
        <Route path="/admin/roles" element={<AdminRolesPage />} />
        <Route path="/admin/permissions" element={<AdminPermissionsPage />} />
        <Route path="/admin/clients" element={<AdminClientsPage />} />
        <Route path="/admin/audit-logs" element={<AuditLogsPage />} />
        <Route path="/admin/settings" element={<SettingsPage />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
