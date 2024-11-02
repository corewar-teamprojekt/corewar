<picture>
 <source media="(prefers-color-scheme: dark)" srcset="./assets/darkmode-titlebar.svg">
 <img alt="picture of game logo" src="./assets/lightmode-titlebar.svg">
</picture>

<p align="center">
    <a href="https://github.com/corewar-teamprojekt/corewar/actions"><img alt="build status" src="https://img.shields.io/github/actions/workflow/status/corewar-teamprojekt/corewar/build-and-deploy.yml"></a>
    <img alt="GitHub commit activity" src="https://img.shields.io/github/commit-activity/m/corewar-teamprojekt/corewar">
    <img alt="GitHub code size in bytes" src="https://img.shields.io/github/languages/code-size/corewar-teamprojekt/corewar">
</p>

## About Corewar
insert infodump
<ul>
<li>a</li>
<li>b</li>
<li>c</li>
<li>d</li>
</ul>

## Start playing
The current production deployment can be found [here](https://corewar.shonk.software/).

We deploy every active branch to https://$BRANCHNAME.corewar.shonk.software/ aswell.

## Local setup

### Prerequisites
 - Install [Node.js](https://nodejs.org/) and the [Node Package Manager](https://www.npmjs.com/get-npm)
 - Install [pre-commit](https://pre-commit.com/), for example with pip  
    ```
    pip install pre-commit
    ```
 - We **recommend** you use [IntelliJ](https://www.jetbrains.com/idea/) as your IDE, since it provides some of the best support for [Kotlin](https://kotlinlang.org/) and [ktor](https://ktor.io/).

### Setting up the project
1. Clone the repo  
    ```
    git clone git@github.com:corewar-teamprojekt/corewar.git
   ```
2. Install our pre-commit hooks with
   ```
   pre-commit install --hook-type commit-msg --hook-type pre-commit
   ```
3. Run the backend tests in `backend` with
   ```
   ./gradlew test
   ```
4. Start the backend by running
    ```
    ./gradlew run
    ```
   in the `backend` directory.
5. Install the frontend dependencies by running
    ```
    npm i
    ```
   in the `frontend` directory.
6. Run the frontend tests by running
   ```
   npm run test
   ```
   in the `frontend` directory.
7. Start the frontend
    ```
    npm run dev
    ```
8. E2E Tests with [Playwright](https://playwright.dev/): In the `e2e` directory, run 
    ```
    npm i
    npm run test
    ```
   NOTE: Depending on your system, you might encounter problems, mostly with Webkit, since playwright only supports some linux distributions!

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.


## License

The Corewar project is open-sourced software licensed under the [Apache License](https://www.apache.org/licenses/LICENSE-2.0.txt). 
