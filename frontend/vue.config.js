module.exports = {
  devServer: {
    client: {
      overlay: {
        warnings: true,
        errors: true
      },
      webSocketURL: {
        port: 3000
      }
    }
  }
}