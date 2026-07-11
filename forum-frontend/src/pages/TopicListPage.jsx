import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import * as topicApi from '../api/topicApi';
import { useAuth } from '../context/AuthContext';

const formatDate = (value) => (value ? new Date(value).toLocaleString() : '');

export default function TopicListPage() {
  const [topics, setTopics] = useState([]);
  const [title, setTitle] = useState('');
  const [error, setError] = useState('');
  const { isAuthenticated } = useAuth();

  const loadTopics = () => {
    topicApi.getAllTopics().then(setTopics).catch(() => setError('Failed to load topics'));
  };

  useEffect(() => {
    loadTopics();
  }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      await topicApi.createTopic({ title });
      setTitle('');
      loadTopics();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create topic');
    }
  };

  return (
    <div style={{ padding: '1rem' }}>
      <h2>Topics</h2>
      {isAuthenticated && (
        <form onSubmit={handleCreate} style={{ marginBottom: '1rem' }}>
          <input
            placeholder="New topic title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
          <button type="submit">Create Topic</button>
        </form>
      )}
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <ul>
        {topics.map((t) => (
          <li key={t.id} style={{ marginBottom: '0.5rem' }}>
            <Link to={`/topics/${t.id}`}>{t.title}</Link> — by {t.author.username} · {t.viewCount} views
            <div style={{ fontSize: '0.8rem', color: '#666' }}>
              Created {formatDate(t.createdAt)}
              {t.modifiedAt !== t.createdAt && <> · modified {formatDate(t.modifiedAt)}</>}
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}