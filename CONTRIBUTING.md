# Contributing
## Merging:
To merge a branch into main, create a pull request. Before merging, at least one other person needs to review the code.

When merging, please use the "rebase" option, to keep the commit history clean.
## Commit message format:
`[#xxx] <message>`  
Where `xxx` is the issue number the commit references.  
This format is enforced by our pre-commit hooks, so before commiting anything to the repository, please install our pre-commit hooks by running:  
## Installing pre-commit
`pip install pre-commit`  
or use your package manager of choice.

## Installing the hooks
`pre-commit install --hook-type commit-msg --hook-type pre-commit`
