import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import * as topicApi from '../api/topicApi';
import * as replyApi from '../api/replyApi';
import { useAuth } from '../context/AuthContext';

const formatDate = (value) => (value ? new Date(value).toLocaleString() : '');

export default function TopicDetailPage() {
  const { id } = useParams();
  const [detail, setDetail] = useState(null);
  const [page, setPage] = useState(0);
  const [content, setContent] = useState('');
  const [error, setError] = useState('');

  const [editingTopic, setEditingTopic] = useState(false);
  const [topicTitle, setTopicTitle] = useState('');

  const [editingReplyId, setEditingReplyId] = useState(null);
  const [editingReplyContent, setEditingReplyContent] = useState('');

  const { isAuthenticated, user } = useAuth();

  const loadDetail = (p) => {
    topicApi.getTopic(id, p).then(setDetail).catch(() => setError('Failed to load topic'));
  };

  useEffect(() => {
    loadDetail(page);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id, page]);

  const handleReply = async (e) => {
    e.preventDefault();
    try {
      await replyApi.createReply(id, { content });
      setContent('');
      loadDetail(page);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to post reply');
    }
  };

  const startEditTopic = () => {
    setTopicTitle(detail.topic.title);
    setEditingTopic(true);
  };

  const saveTopicEdit = async (e) => {
    e.preventDefault();
    try {
      await topicApi.updateTopic(id, { title: topicTitle });
      setEditingTopic(false);
      loadDetail(page);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update topic');
    }
  };

  const startEditReply = (reply) => {
    setEditingReplyId(reply.id);
    setEditingReplyContent(reply.content);
  };

  const saveReplyEdit = async (e, replyId) => {
    e.preventDefault();
    try {
      await replyApi.updateReply(replyId, { content: editingReplyContent });
      setEditingReplyId(null);
      loadDetail(page);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update reply');
    }
  };

  if (!detail) return <p style={{ padding: '1rem' }}>{error || 'Loading...'}</p>;

  const isTopicAuthor = user && user.id === detail.topic.author.id;
  const isModerator = user && (user.role === 'MODERATOR' || user.role === 'ADMIN');
  const canEditTopic = isTopicAuthor || isModerator;

  return (
    <div style={{ padding: '1rem' }}>
      {editingTopic ? (
        <form onSubmit={saveTopicEdit}>
          <input
            value={topicTitle}
            onChange={(e) => setTopicTitle(e.target.value)}
            maxLength={200}
            required
          />
          <button type="submit">Save</button>
          <button type="button" onClick={() => setEditingTopic(false)}>Cancel</button>
        </form>
      ) : (
        <h2>
          {detail.topic.title}
          {canEditTopic && (
            <button style={{ marginLeft: '0.5rem' }} onClick={startEditTopic}>Edit</button>
          )}
        </h2>
      )}
      <p>By {detail.topic.author.username} · {detail.topic.viewCount} views</p>
      <p style={{ fontSize: '0.8rem', color: '#666' }}>
        Created {formatDate(detail.topic.createdAt)}
        {detail.topic.modifiedAt !== detail.topic.createdAt && <> · modified {formatDate(detail.topic.modifiedAt)}</>}
      </p>

      <h3>Replies ({detail.totalReplies})</h3>
      <ul>
        {detail.replies.map((r) => {
          const isReplyAuthor = user && user.id === r.author.id;
          const canEditReply = isReplyAuthor || isModerator;
          return (
            <li key={r.id} style={{ marginBottom: '0.5rem' }}>
              {editingReplyId === r.id ? (
                <form onSubmit={(e) => saveReplyEdit(e, r.id)}>
                  <textarea
                    value={editingReplyContent}
                    onChange={(e) => setEditingReplyContent(e.target.value)}
                    required
                  />
                  <br />
                  <button type="submit">Save</button>
                  <button type="button" onClick={() => setEditingReplyId(null)}>Cancel</button>
                </form>
              ) : (
                <>
                  <strong>{r.author.username}:</strong> {r.content}
                  {canEditReply && (
                    <button style={{ marginLeft: '0.5rem' }} onClick={() => startEditReply(r)}>Edit</button>
                  )}
                  <div style={{ fontSize: '0.8rem', color: '#666' }}>
                    Posted {formatDate(r.createdAt)}
                    {r.modifiedAt !== r.createdAt && <> · edited {formatDate(r.modifiedAt)}</>}
                  </div>
                </>
              )}
            </li>
          );
        })}
      </ul>

      <div>
        <button disabled={page <= 0} onClick={() => setPage((p) => p - 1)}>Prev</button>
        <span> Page {detail.page + 1} of {detail.totalPages || 1} </span>
        <button disabled={page + 1 >= detail.totalPages} onClick={() => setPage((p) => p + 1)}>Next</button>
      </div>

      {isAuthenticated && (
        <form onSubmit={handleReply} style={{ marginTop: '1rem' }}>
          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="Write a reply..."
            required
          />
          <br />
          <button type="submit">Reply</button>
        </form>
      )}
      {error && <p style={{ color: 'red' }}>{error}</p>}
    </div>
  );
}