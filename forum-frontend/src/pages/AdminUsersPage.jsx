import { useEffect, useState } from 'react';
import * as userApi from '../api/userApi';

// Admins can only assign USER or MODERATOR (backend rejects assigning ADMIN)
const ASSIGNABLE_ROLES = ['USER', 'MODERATOR'];

export default function AdminUsersPage() {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState('');

  const loadUsers = () => {
    userApi.getAllUsers().then(setUsers).catch(() => setError('Failed to load users'));
  };

  useEffect(() => {
    loadUsers();
  }, []);

  const handleRoleChange = async (id, role) => {
    try {
      await userApi.assignRole(id, role);
      loadUsers();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update role');
    }
  };

  return (
    <div style={{ padding: '1rem' }}>
      <h2>Manage Users</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <table border="1" cellPadding="6" style={{ borderCollapse: 'collapse' }}>
        <thead>
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Email</th>
            <th>Role</th>
          </tr>
        </thead>
        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.id}</td>
              <td>{u.username}</td>
              <td>{u.email}</td>
              <td>
                {u.role === 'ADMIN' ? (
                  'ADMIN'
                ) : (
                  <select value={u.role} onChange={(e) => handleRoleChange(u.id, e.target.value)}>
                    {ASSIGNABLE_ROLES.map((r) => (
                      <option key={r} value={r}>{r}</option>
                    ))}
                  </select>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}