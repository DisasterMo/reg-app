const PROXY_CONFIG = [
  {
      context: [
          "/rest",
          "/Shibboleth.sso",
      ],
      target: "https://bwidm.scc.kit.edu",
      secure: false
  }
]

module.exports = PROXY_CONFIG;
