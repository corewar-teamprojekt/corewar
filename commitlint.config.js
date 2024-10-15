module.exports = {
  rules: {
    "header-match-ticket": [2, "always"],
  },
  plugins: [
    {
      rules: {
        "header-match-ticket": ({ header }) => {
          const ticketPattern = /^\[(#\d+(,#\d+)*)?] /;
          return [
            ticketPattern.test(header),
            `Commit message must start with a ticket number like "[#123]"`,
          ];
        },
      },
    },
  ],
};
