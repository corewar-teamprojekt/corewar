# Contributing
## Merging:
To merge a branch into main, create a pull request. Before merging, at least one other person needs to review the code.

When merging, please use the "rebase" option, to keep our commit history clean.
## Commit message format:
`[#xxx] <message>`  
Where `xxx` is the issue number the commit references.  
This format is enforced by our pre-commit hooks, so before commiting anything to the repository, please install our pre-commit hooks by running:  

## Project Development Requirements
### JDK
The backend requires JDK 21 or higher to be installed.
### Node.js
The frontend requires Node.js to be installed.
### pre-commit
Our pre-commit hooks require Python to be installed.


## Setting up the project
### IDE Plugins
We recommend using the following plugins for your IDE:
- [Kotlin](https://kotlinlang.org/docs/kotlin-ide.html)
- [ESLint](https://eslint.org/docs/latest/use/integrations)
- [Prettier](https://prettier.io/docs/en/editors.html)
- [ktfmt](https://github.com/facebook/ktfmt)

### The easy way
To set up the project, use our taskfile.
To install the taskfile, read the instructions [here](https://taskfile.dev/installation).
Then, to set up the project, run:
`task setup`

## Using the taskfile
The taskfile is a simple way to run common tasks in the project.
To see all available tasks, run `task --list`.
Here's an overview of tasks, that are available for the frontend AND backend:
- `setup` - Sets up the project.
- `run` - Runs the project.
- `test` - Runs the tests.
- `lint` - Lints the code.
- `format` - Formats the code.
- `format-check` - Checks if the code is formatted correctly.
- `clean` - Cleans the project.

These can be run for the backend and frontend separately by using the `backend:` or `frontend:` prefix or by running the task in the respective directory.
Running `task <taskname>` in the project root will run the task for both the backend and frontend.

Some tasks are only available for the entire project:
- `build-container` - Builds the container.
- `run-container` - Runs the container.

## The manual way (not recommended)
### pre-commit
- [Install pre-commit](https://pre-commit.com/#install)
- Install the pre-commit hooks by running `pre-commit install --hook-type commit-msg --hook-type pre-commit`

<details>
  <summary>What do the pre-commit hooks check?</summary>
  
    - `ktfmt` - Checks if the Kotlin code is formatted correctly.
    - `commit message linting` - Checks if the commit message is in the correct format.
    - `prettier` - Checks if the JavaScript code is formatted correctly.
    - `eslint` - Checks if the JavaScript code follows the rules defined in the `.eslintrc.js` file.
</details>

### Running the project
#### Backend
To run the backend, navigate to the `backend` directory and run:  
`./gradlew api:run`
The backend will be available at `http://localhost:8080`.
Our Interpreter is automatically compiled as a dependency of the backend.
#### Frontend
To run the frontend, navigate to the `frontend/app` directory and run:
`npm install` (if you haven't started the project yet)
`npm start`

### Testing
#### Backend
To run the backend tests, navigate to the `backend` directory and run:  
`./gradlew test`
#### Frontend
To run the frontend tests, navigate to the `frontend/app` directory and run:
`npm test`

### Linting and Formatting
#### Backend
To lint the backend, navigate to the `backend` directory and run:  
`./gradlew ktfmtCheck`
#### Frontend
To lint the frontend, navigate to the `frontend/app` directory and run:
`npm run lint` and `npx prettier --check .`
