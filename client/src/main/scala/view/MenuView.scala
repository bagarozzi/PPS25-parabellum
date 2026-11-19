package it.unibo.parabellum
package view

import protocol.AccountRules
import session.OnlineAccount
import util.{MapGenerator, PlayerNames}
import scalafx.scene.Node
import scalafx.scene.layout.{BorderPane, HBox, Region, StackPane, VBox}
import scalafx.scene.control.{Button, Label, PasswordField, TextField, TextInputControl, Tooltip}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.image.{Image, ImageView}

/**
 * Represents the Main Menu scene of the game.
 * It lets the user choose how many players take part in the match (from [[MapGenerator.MinPlayers]]
 * to [[MapGenerator.MaxPlayers]]), customize their names and the number of soldiers per team.
 *
 * On the side there is the account of the user on a server: he logs in (or registers) here, and only
 * then he can play online. Once he is logged in, he can log out from here too.
 *
 * @param onPlayClicked Callback function executed when the "Play" button is clicked.
 *                      It provides the names of the players, in turn order, and the number of soldiers per team.
 * @param onShootingRangeClicked Callback function executed when the "Shooting range" button is clicked.
 * @param onOnlineClicked Callback function executed when the "Play online" button is clicked.
 * @param onLogin    Callback executed to log in. It provides the server, the username and the password.
 * @param onRegister Callback executed to create an account. It provides the server, the username and the password.
 * @param onLogout   Callback executed to log out.
 * @param account    The user that is logged in, if any.
 * @param server     The server to suggest for the login.
 */
class MenuView(
    onPlayClicked: (Seq[String], Int) => Unit,
    onShootingRangeClicked: () => Unit,
    onOnlineClicked: () => Unit,
    onLogin: (String, String, String) => Unit,
    onRegister: (String, String, String) => Unit,
    onLogout: () => Unit,
    account: Option[OnlineAccount],
    server: String
) extends StackPane:
  private val backgroundUrl = getClass.getResource("/GameMenu.jpeg")
  if backgroundUrl == null then
    throw new RuntimeException("File /GameMenu.jpeg not found in resources")

  private val backgroundImage = new Image(backgroundUrl.toExternalForm)

  private val backgroundView = new ImageView(backgroundImage):
    fitWidth <== MenuView.this.width
    fitHeight <== MenuView.this.height
    preserveRatio = false

  private val rowLabelStyle = "-fx-text-fill: white; -fx-font-size: 16pt; -fx-font-weight: bold;"
  private val buttonStyle = "-fx-font-size: 20pt; -fx-font-weight: bold; -fx-padding: 10 40 10 40; -fx-cursor: hand;"
  private val playerCounts = MapGenerator.MinPlayers to MapGenerator.MaxPlayers

  private var playerCount = MapGenerator.MinPlayers

  private def textInput(startingText: String): TextField =
    new TextField:
      text = startingText
      maxWidth = 200

  private def row(labelText: String, field: TextField): HBox =
    val rowLabel = new Label(labelText):
      style = rowLabelStyle
    new HBox:
      alignment = Pos.Center
      spacing = 10
      children = List[Node](rowLabel, field)

  // one input for every possible player: only the first `playerCount` rows are shown
  private val nameInputs: Vector[TextField] =
    (1 to MapGenerator.MaxPlayers).toVector.map(i => textInput(s"Player $i"))

  private val nameRows: Vector[HBox] =
    nameInputs.zipWithIndex.map((field, index) => row(s"Player ${index + 1}:", field))

  private val soldiersInput = textInput("1")
  private val soldiersRow = row("Soldiers per player:", soldiersInput)

  private def countStyle(count: Int): String =
    val base = "-fx-font-size: 14pt; -fx-font-weight: bold; -fx-cursor: hand;"
    if count == playerCount then s"$base -fx-background-color: #f0c040;" else base

  private def countButton(count: Int): Button =
    new Button(s"$count players"):
      style = countStyle(count)
      onAction = _ => selectPlayerCount(count)

  private val countButtons: Vector[Button] = playerCounts.toVector.map(countButton)

  private val countRow = new HBox:
    alignment = Pos.Center
    spacing = 10
    children = countButtons

  private val playButton = new Button("Play"):
    style = buttonStyle
    onAction = _ => {
      val names = PlayerNames.normalize(nameInputs.take(playerCount).map(_.text.value))
      val soldiersCount = soldiersInput.text.value.toIntOption.getOrElse(1).max(1)
      onPlayClicked(names, soldiersCount)
    }

  private val onlineButton = new Button("Play online"):
    style = buttonStyle
    onAction = _ => onOnlineClicked()

  private val shootingRangeButton = new Button("Shooting range"):
    style = buttonStyle
    onAction = _ => onShootingRangeClicked()

  private def layoutNodes: List[Node] =
    List[Node](countRow) ++ nameRows.take(playerCount) ++ List[Node](soldiersRow, playButton, onlineButton, shootingRangeButton)

  private def selectPlayerCount(count: Int): Unit =
    playerCount = count
    playerCounts.zip(countButtons).foreach((n, button) => button.style = countStyle(n))
    menuLayout.children = layoutNodes

  private val menuLayout = new VBox:
    alignment = Pos.Center
    spacing = 12
    children = layoutNodes

  // ---------------------------------------------------------------- account

  private val panelStyle = "-fx-background-color: rgba(0, 0, 0, 0.6); -fx-background-radius: 12;"
  private val panelTitleStyle = "-fx-text-fill: white; -fx-font-size: 18pt; -fx-font-weight: bold;"
  private val panelLabelStyle = "-fx-text-fill: white; -fx-font-size: 12pt; -fx-font-weight: bold;"
  private val panelTextStyle = "-fx-text-fill: white; -fx-font-size: 13pt;"
  private val panelButtonStyle = "-fx-font-size: 13pt; -fx-font-weight: bold; -fx-padding: 6 16 6 16; -fx-cursor: hand;"
  private val messageStyle = "-fx-text-fill: #ffd54f; -fx-font-size: 11pt; -fx-font-weight: bold;"
  private val fieldWidth = 220

  private val serverField = new TextField:
    text = server
    maxWidth = fieldWidth
  private val usernameField = new TextField:
    maxWidth = fieldWidth
  private val passwordField = new PasswordField:
    maxWidth = fieldWidth
    onAction = _ => loginPressed()

  private val messageLabel = new Label(""):
    style = messageStyle
    wrapText = true
    maxWidth = fieldWidth

  private val loginButton = new Button("Login"):
    style = panelButtonStyle
    onAction = _ => loginPressed()

  private val registerButton = new Button("Register"):
    style = panelButtonStyle
    onAction = _ => registerPressed()

  private val logoutButton = new Button("Logout"):
    style = panelButtonStyle
    onAction = _ => onLogout()

  private val loggedInLabel = new Label(""):
    style = panelTextStyle
    wrapText = true
    maxWidth = fieldWidth

  private def field(labelText: String, input: TextInputControl): VBox =
    new VBox:
      spacing = 2
      children = List[Node](new Label(labelText) { style = panelLabelStyle }, input)

  private val panelTitle = new Label("Account"):
    style = panelTitleStyle

  private val accountButtons = new HBox:
    spacing = 10
    children = List[Node](loginButton, registerButton)

  private val loggedOutNodes: List[Node] = List(
    panelTitle,
    field("Server", serverField),
    field("Username", usernameField),
    field("Password", passwordField),
    accountButtons,
    messageLabel
  )

  private val loggedInNodes: List[Node] = List(panelTitle, loggedInLabel, logoutButton, messageLabel)

  private val accountPanel = new VBox:
    spacing = 10
    padding = Insets(16)
    alignment = Pos.TopLeft
    style = panelStyle
    prefWidth = fieldWidth + 32
    maxHeight = Region.USE_PREF_SIZE

  /** The server and the credentials, if they make sense. */
  private def credentials: Either[String, (String, String, String)] =
    val address = serverField.text.value.trim
    val username = AccountRules.normalize(usernameField.text.value)
    val password = passwordField.text.value
    if address.isEmpty then Left("Write the address of the server, for example 127.0.0.1:8080")
    else if !AccountRules.isValidUsername(username) then Left(s"The username is not valid: ${AccountRules.UsernameRule}")
    else if !AccountRules.isValidPassword(password) then Left(s"The password is not valid: ${AccountRules.PasswordRule}")
    else Right((address, username, password))

  private def loginPressed(): Unit = withCredentials(onLogin)

  private def registerPressed(): Unit = withCredentials(onRegister)

  private def withCredentials(action: (String, String, String) => Unit): Unit =
    credentials match
      case Left(message) => showMessage(message)
      case Right((address, username, password)) =>
        showMessage("")
        action(address, username, password)

  /** Shows that a user is logged in: he can log out and play online. */
  def showLoggedIn(account: OnlineAccount): Unit =
    loggedInLabel.text = s"Logged in as ${account.username}\non ${account.server}"
    passwordField.text = ""
    accountPanel.children = loggedInNodes
    onlineButton.disable = false
    onlineButton.tooltip = null

  /** Shows that nobody is logged in: he can log in or register, but not play online. */
  def showLoggedOut(): Unit =
    accountPanel.children = loggedOutNodes
    onlineButton.disable = true
    onlineButton.tooltip = Tooltip("Log in to play online")

  /** Shows a message to the user, for example why the login failed. An empty message removes it. */
  def showMessage(message: String): Unit =
    messageLabel.text = message

  /** While the client is talking to the server the buttons are disabled, so that nothing is done twice. */
  def setBusy(busy: Boolean): Unit =
    loginButton.disable = busy
    registerButton.disable = busy
    logoutButton.disable = busy
    messageLabel.text = if busy then "Connecting..." else ""

  account match
    case Some(user) => showLoggedIn(user)
    case None => showLoggedOut()

  private val content = new BorderPane:
    center = menuLayout
    right = accountPanel
    padding = Insets(20)

  BorderPane.setAlignment(accountPanel, Pos.TopRight)

  children = List(backgroundView, content)
