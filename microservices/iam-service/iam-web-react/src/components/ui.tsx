import type { ReactNode } from 'react';
import { useI18n } from '../i18n';

export function Card({
  title,
  subtitle,
  children,
  actions,
}: {
  title: string;
  subtitle?: string;
  children: ReactNode;
  actions?: ReactNode;
}) {
  return (
    <section className="card">
      <header className="card__header">
        <h2>{title}</h2>
        {subtitle ? <p className="subtitle">{subtitle}</p> : null}
      </header>
      {children}
      {actions ? <div className="actions">{actions}</div> : null}
    </section>
  );
}

export function Field({
  label,
  children,
}: {
  label: string;
  children: ReactNode;
}) {
  return (
    <label className="field">
      <span className="label">{label}</span>
      {children}
    </label>
  );
}

export function Alert({
  kind,
  children,
}: {
  kind: 'error' | 'ok' | 'info';
  children: ReactNode;
}) {
  return <div className={`alert alert--${kind}`}>{children}</div>;
}

export function Loading() {
  const { t } = useI18n();

  return (
    <div className="loading">
      <span className="spinner" />
      {t('common.loading')}
    </div>
  );
}

export function ErrorBox({
  message,
  onRetry,
}: {
  message: string;
  onRetry?: () => void;
}) {
  const { t } = useI18n();

  return (
    <div className="alert alert--error">
      <div style={{ marginBottom: onRetry ? 10 : 0 }}>{message}</div>
      {onRetry ? (
        <button type="button" className="btn btn--sm btn--secondary" onClick={onRetry}>
          {t('common.retry')}
        </button>
      ) : null}
    </div>
  );
}

export function Empty({ message }: { message?: string }) {
  const { t } = useI18n();
  return <p className="subtitle">{message ?? t('common.empty')}</p>;
}

export function Pagination({
  page,
  totalPages,
  onChange,
}: {
  page: number;
  totalPages: number;
  onChange: (page: number) => void;
}) {
  if (totalPages <= 1) {
    return null;
  }

  return (
    <div className="pager">
      <span>
        Page {page + 1} / {totalPages}
      </span>
      <div className="pager__buttons">
        <button
          type="button"
          className="btn btn--sm btn--secondary"
          disabled={page === 0}
          onClick={() => onChange(page - 1)}
        >
          Prev
        </button>
        <button
          type="button"
          className="btn btn--sm btn--secondary"
          disabled={page + 1 >= totalPages}
          onClick={() => onChange(page + 1)}
        >
          Next
        </button>
      </div>
    </div>
  );
}

export function formatDateTime(value: string | null | undefined): string {
  if (!value) {
    return '—';
  }

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return '—';
  }

  return date.toLocaleString(undefined, {
    year: 'numeric',
    month: 'short',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
}
