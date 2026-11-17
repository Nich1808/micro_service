# iam-web-react

React dashboard for the ISTAD IAM service: account management, security, sessions and
activity. Talks to the REST API with OAuth 2.1 access tokens obtained through the
authorization-code + PKCE flow.

- React 18 · TypeScript · Vite 6 · React Router
- No auth library: PKCE is implemented with the browser's WebCrypto
- English and Khmer, switchable at runtime

## Run

```bash
cp .env.example .env      # optional, defaults match a local backend
npm install
npm run dev               # http://localhost:5173
```

The backend must be running on `http://localhost:9026` with:

- `iam.bootrap.spa.redirect-uri` (default `http://localhost:5173/callback`) registered on
  the `oidc-client` client — the seeder does this on a fresh database;
- the dashboard origin allowed by CORS (`iam.web.allowed-origins`, default includes
  `http://localhost:5173`).

## Pages

| Route | Purpose |
| --- | --- |
| `/` | Sign in (starts the PKCE flow) |
| `/callback` | Completes the flow and stores tokens |
| `/profile` | View and update name, username, email, phone, picture and address |
| `/security` | Change password |
| `/accounts` | View, link and unlink social accounts |
| `/sessions` | Active sessions with device, browser, IP, last activity; terminate one or all |
| `/login-history` | Successful and failed sign-in attempts |
| `/activity` | Security events (password change, profile update, session revoked, …) |
| `/deactivate` | Request account deactivation |

## Scripts

```bash
npm run dev        # development server
npm run build      # type-check and production bundle in dist/
npm run preview    # serve the production bundle
npm run typecheck  # tsc --noEmit
```

## Notes

- Tokens live in `sessionStorage`, so closing the tab signs the user out.
- A 401 triggers one refresh attempt; if that fails the tokens are cleared and the user is
  sent back to the sign-in page.
- API errors are rendered from the RFC 9457 problem details returned by the service.
