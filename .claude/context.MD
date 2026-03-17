# Project Context
This is a UI that helps a manager to select the best place to install the logistic hub based on calculation of barycenter logistic.
It stores it, and exposes a simple API.

Main parts:
- Storage layer (database + object storage)
- API service
- Lightweight UI

Conventions:
- Config via environment variables
- Error handling with structured logs
- CI runs tests and lint on every PR

Dependencies:
- Runtime: Python 3.11
- Package: requests, pydantic
- Tools: Docker, Makefile for common tasks

# Code style
- Use ES modules (import/export) syntax, not CommonJS (require)
- Destructure imports when possible (eg. import { foo } from 'bar')

# Workflow
- Be sure to typecheck when you're done making a series of code changes
- Prefer running single tests, and not the whole test suite, for performance