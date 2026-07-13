# BetterHR AI API Setup

The chatbot works without an API key. Static FAQ and role-aware database answers always run first.
Gemini is called only when the resolved intent is `fallback`.

## Tomcat On Windows

1. Open `scripts/tomcat-setenv-gemini.example.bat`.
2. Replace `PASTE_GEMINI_API_KEY_HERE` with the real key.
3. Copy the file to `<TOMCAT_HOME>\bin\setenv.bat`.
4. If `setenv.bat` already exists, copy only the `GEMINI_*` lines into it instead of replacing the file.
5. Restart Tomcat.

Default configuration:

```bat
set "GEMINI_API_KEY=PASTE_GEMINI_API_KEY_HERE"
set "GEMINI_MODEL=gemini-3.1-flash-lite"
```

Optional endpoint override:

```bat
set "GEMINI_API_URL=https://generativelanguage.googleapis.com/v1beta/models/"
```

Never put the real API key in JSP, JavaScript, Git, database FAQ content, or screenshots.

## Runtime Behavior

- Missing key: the existing Vietnamese fallback is returned.
- Provider timeout/error: the existing fallback is returned.
- Known FAQ: no provider call.
- Role-aware question: filtered DAO answer is returned without a provider call.
- Sensitive value detected: no provider call.
- AI receives only the current fallback question and role label, not raw database records or chat history.
