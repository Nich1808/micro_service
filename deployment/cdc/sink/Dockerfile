# Pinned to the bookworm variant: debian:trixie-based images fail with
# "exec format error" on this Docker VM, and pinning keeps the build reproducible.
FROM python:3.12-slim-bookworm

WORKDIR /app

ENV PYTHONUNBUFFERED=1 \
    PYTHONDONTWRITEBYTECODE=1

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY elasticsearch_sink.py .

CMD ["python", "-u", "elasticsearch_sink.py"]
