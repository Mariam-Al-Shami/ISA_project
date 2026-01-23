import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

public class SenetGUI extends JFrame {
    
    private JPanel boardPanel;
    private JPanel infoPanel;
    private JLabel statusLabel;
    private JLabel rollLabel;
    private JLabel turnLabel;
    private JButton rollButton;
    private JButton skipButton;
    
    private State gameState;
    private int selectedPiece = -1;
    private int currentRoll = 0;
    private int targetSquare = -1;
    
    private static final Color BOARD_COLOR = new Color(210, 180, 140);
    private static final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private static final Color DARK_SQUARE = new Color(181, 136, 99);
    private static final Color BLACK_PIECE = new Color(50, 50, 50);
    private static final Color WHITE_PIECE = new Color(230, 230, 230);
    private static final Color HIGHLIGHT_COLOR = new Color(255, 215, 0, 100);
    private static final Color MOVEABLE_HIGHLIGHT = new Color(220, 200, 0);
    private static final Color TARGET_HIGHLIGHT = new Color(0, 255, 0, 140);
    private static final Color EXIT_SQUARE_COLOR = new Color(240, 217, 181);
    
    private static final int SQUARE_SIZE = 70;
    private static final int PIECE_SIZE = 35;
    private static final int MARGIN = 75;
    
    private Point[] squarePositions = new Point[31];
    
    private HashMap<Integer, ImageIcon> squareIcons;
    
    private JPanel blackOutPanel;
    private JPanel whiteOutPanel;
    
    private HashSet<Integer> movablePieces = new HashSet<>();
    
    public SenetGUI() {
        showStartList();
    }
    
    private void showStartList() {
        String[] options = {"Human (White)", "Computer (Black)"};
        int choice = JOptionPane.showOptionDialog(
            null,
            "Choose who starts the game:",
            "Start Game",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        gameState = new State();
        if (choice == 1) {
            gameState.isBlackTurn = true;
        } else {
            gameState.isBlackTurn = false;
        }
        initSquarePos();
        initSquareIcons();
        setupGUI();
        updateDisplay();
        if (gameState.isBlackTurn) {
            startComputerTurn();
        }
    }
    
    private void initSquarePos() {
        for (int i = 0; i < 10; i++) {
            squarePositions[i] = new Point(
                MARGIN + i * SQUARE_SIZE,
                MARGIN);
            }
        for (int i = 10; i < 20; i++) {
            squarePositions[i] = new Point(
                MARGIN + (19 - i) * SQUARE_SIZE,
                MARGIN + SQUARE_SIZE);
            }
        for (int i = 20; i < 30; i++) {
            squarePositions[i] = new Point(
                MARGIN + (i - 20) * SQUARE_SIZE,
                MARGIN + 2 * SQUARE_SIZE);
            }
        squarePositions[30] = new Point(
            MARGIN + 10 * SQUARE_SIZE + 20,
            MARGIN + 2 * SQUARE_SIZE);
        }
    

    private void initSquareIcons() {
        squareIcons = new HashMap<>();
        
        int[] specialSquares = {14, 25, 26, 27, 28, 29};
        String[] imageFiles = {
            "rebirth.png", "barrier.png", "water.png", "three.png", "two.png", "freedom.png"
        };
        
        for (int i = 0; i < specialSquares.length; i++) {
            ImageIcon icon = loadImageIcon("images/" + imageFiles[i], 50, 50);
            if (icon != null) {
                squareIcons.put(specialSquares[i], icon);
            }
        }
    }
    
    private ImageIcon loadImageIcon(String path, int width, int height) {
        try {
            File file = new File(path);
            if (file.exists()) {
                Image image = ImageIO.read(file);
                Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
            
            URL url = getClass().getResource("/" + path);
            if (url != null) {
                Image image = ImageIO.read(url);
                Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private void setupGUI() {
        setTitle("Senet Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        infoPanel.setBackground(new Color(70, 50, 30));
        
        turnLabel = new JLabel("", JLabel.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 18));
        turnLabel.setForeground(Color.WHITE);
        
        rollLabel = new JLabel("Roll: 0", JLabel.CENTER);
        rollLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        rollLabel.setForeground(Color.YELLOW);
        
        statusLabel = new JLabel("Press 'Roll Sticks' to start your turn", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(Color.WHITE);
        
        rollButton = new JButton("Roll Sticks");
        rollButton.setFont(new Font("Arial", Font.BOLD, 14));
        rollButton.setBackground(new Color(13, 100, 100));
        rollButton.setForeground(Color.black);
        rollButton.addActionListener(e -> rollDice());
        
        skipButton = new JButton("Skip");
        skipButton.setFont(new Font("Arial", Font.BOLD, 14));
        skipButton.setBackground(new Color(100, 100, 100));
        skipButton.setForeground(Color.BLACK);
        skipButton.addActionListener(e -> skipTurn());
        skipButton.setEnabled(false);
        
        infoPanel.add(turnLabel);
        infoPanel.add(rollButton);
        infoPanel.add(rollLabel);
        infoPanel.add(skipButton);
        infoPanel.add(statusLabel);
        
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
                drawPieces(g);
                drawSpecialIcons(g);
                drawExitSquare(g);
                
                if (currentRoll > 0 && !gameState.isBlackTurn) {
                    drawMovablePiecesHighlight(g);
                }
                
                if (selectedPiece != -1) {
                    drawHighlight(g, selectedPiece);
                    if (targetSquare != -1) {
                        drawTargetHighlight(g, targetSquare);
                    }
                }
            }
        };
        
        boardPanel.setPreferredSize(new Dimension(
            10 * SQUARE_SIZE + 2 * MARGIN + 40,
            3 * SQUARE_SIZE + 2 * MARGIN + 50
        ));
        boardPanel.setBackground(BOARD_COLOR);
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!gameState.isBlackTurn) {
                    handleBoardClick(e.getX(), e.getY());
                }
            }
        });
        
        JPanel outPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        outPanel.setBackground(new Color(70, 50, 30));
        
        blackOutPanel = OutPiecesPanel("out Black (Computer)", BLACK_PIECE, true);
        whiteOutPanel = OutPiecesPanel("out White (Human)", WHITE_PIECE, false);
        
        outPanel.add(whiteOutPanel);
        outPanel.add(blackOutPanel);
        
        add(infoPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(outPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JPanel OutPiecesPanel(String title, Color pieceColor, boolean isBlack) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(100, 80, 60));
        panel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        
        JPanel piecesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int outCount = isBlack ? gameState.blackOut : gameState.whiteOut;
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                
                for (int i = 0; i < outCount; i++) {
                    drawPiece(g, 
                        i * (PIECE_SIZE + 5) + 10, 
                        10, 
                        pieceColor, 
                        isBlack ? "B" : "W"
                    );
                }
            }
        };
        
        piecesPanel.setPreferredSize(new Dimension(300, 70));
        piecesPanel.setBackground(new Color(120, 100, 80));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(piecesPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void drawBoard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (int i = 0; i < 30; i++) {
            Point pos = squarePositions[i];
            int row = i / 10;
            int col = i % 10;
            
            if (row == 1) col = 9 - col;

            boolean isLight = ((row + col) % 2 == 0);
            g2d.setColor(isLight ? LIGHT_SQUARE : DARK_SQUARE);
            g2d.fillRect(pos.x, pos.y, SQUARE_SIZE, SQUARE_SIZE);
            
            g2d.setColor(Color.BLACK);
            g2d.drawRect(pos.x, pos.y, SQUARE_SIZE, SQUARE_SIZE);
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 13));
            String num = String.valueOf(i + 1);
            FontMetrics fm = g2d.getFontMetrics();
            int numX = pos.x + (SQUARE_SIZE - fm.stringWidth(num)) / 2;
            int numY = pos.y + SQUARE_SIZE - 8;
            
            g2d.setColor(isLight ? Color.BLACK : Color.WHITE);
            g2d.drawString(num, numX, numY);
        }
    }
    
    private void drawExitSquare(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  
        Point pos = squarePositions[30];
        
        g2d.setColor(EXIT_SQUARE_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.fillRect(pos.x, pos.y, SQUARE_SIZE, SQUARE_SIZE);
        
        g2d.setColor(Color.BLACK);
        g2d.drawRect(pos.x, pos.y, SQUARE_SIZE, SQUARE_SIZE);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        String label = "Exit";
        FontMetrics fm = g2d.getFontMetrics();
        int labelX = pos.x + (SQUARE_SIZE - fm.stringWidth(label)) / 2;
        int labelY = pos.y + SQUARE_SIZE / 2 + 5;
        g2d.setColor(Color.BLACK);
        g2d.drawString(label, labelX, labelY);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 13));
        String num = "31";
        int numX = pos.x + (SQUARE_SIZE - fm.stringWidth(num)) / 2;
        int numY = pos.y + SQUARE_SIZE - 8;
        g2d.drawString(num, numX, numY);
    }
    
    private void drawSpecialIcons(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int[] specialSquares = {14, 25, 26, 27, 28, 29};
        
        for (int square : specialSquares) {
            Point pos = squarePositions[square];
            
            if (squareIcons.containsKey(square)) {
                ImageIcon icon = squareIcons.get(square);
                int iconX = pos.x + (SQUARE_SIZE - icon.getIconWidth()) / 2;
                int iconY = pos.y + 10;
                icon.paintIcon(boardPanel, g2d, iconX, iconY);
            }
        }
    }
    
    private void drawPieces(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (int i = 0; i < 31; i++) {
            int piece = (i < 30) ? gameState.board[i] : 0;
            if (piece != 0) {
                Point pos = squarePositions[i];
                int centerX = pos.x + SQUARE_SIZE / 2;
                int centerY = pos.y + SQUARE_SIZE / 2;
                
                Color pieceColor = (piece == 1) ? BLACK_PIECE : WHITE_PIECE;
                String pieceLabel = (piece == 1) ? "B" : "W";
                
                drawPiece(g2d, centerX - PIECE_SIZE/2, centerY - PIECE_SIZE/2, 
                         pieceColor, pieceLabel);
            }
        }
    }
    
    private void drawPiece(Graphics g, int x, int y, Color color, String label) {
        Graphics2D g2d = (Graphics2D) g;
        
        GradientPaint gradient = new GradientPaint(
            x, y, color.brighter(),
            x + PIECE_SIZE, y + PIECE_SIZE, color.darker()
        );
        g2d.setPaint(gradient);
        g2d.fillOval(x, y, PIECE_SIZE, PIECE_SIZE);
        
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x, y, PIECE_SIZE, PIECE_SIZE);
        
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.drawOval(x + 1, y + 1, PIECE_SIZE - 2, PIECE_SIZE - 2);
        
        g2d.setColor((color == BLACK_PIECE) ? Color.WHITE : Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        int labelX = x + (PIECE_SIZE - fm.stringWidth(label)) / 2;
        int labelY = y + (PIECE_SIZE + fm.getAscent()) / 2 - 3;
        g2d.drawString(label, labelX, labelY);
    }
    
    private void drawHighlight(Graphics g, int squareIndex) {
        if (squareIndex < 0 || squareIndex >= 30) return;
        
        Graphics2D g2d = (Graphics2D) g;
        Point pos = squarePositions[squareIndex];
        
        g2d.setColor(HIGHLIGHT_COLOR);
        g2d.drawRect(pos.x + 3, pos.y + 3, SQUARE_SIZE - 6, SQUARE_SIZE - 6);
        
        g2d.setColor(new Color(255, 100, 0, 40));
        g2d.fillRect(pos.x + 3, pos.y + 3, SQUARE_SIZE - 6, SQUARE_SIZE - 6);
    }
    
    private void drawMovablePiecesHighlight(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        updateMovablePieces();
        
        for (int square : movablePieces) {
            if (square >= 0 && square < 30) {
                Point pos = squarePositions[square];
                
                g2d.setColor(MOVEABLE_HIGHLIGHT);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRect(pos.x + 2, pos.y + 2, SQUARE_SIZE - 4, SQUARE_SIZE - 4);
            }
        }
    }
    
    private void drawTargetHighlight(Graphics g, int squareIndex) {
        if (squareIndex < 0 || squareIndex >= 31) return;
        
        Graphics2D g2d = (Graphics2D) g;
        Point pos = squarePositions[squareIndex];
        
        g2d.setColor(TARGET_HIGHLIGHT);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(pos.x + 3, pos.y + 3, SQUARE_SIZE - 6, SQUARE_SIZE - 6);
        
        g2d.setColor(new Color(0, 255, 0, 60));
        g2d.fillRect(pos.x + 3, pos.y + 3, SQUARE_SIZE - 6, SQUARE_SIZE - 6);
    }
    
    private void updateMovablePieces() {
        movablePieces.clear();
        
        if (currentRoll == 0 || gameState.isBlackTurn) {
            return;
        }
        
        for (int i = 0; i < 30; i++) {
            int piece = gameState.board[i];
            if (piece == 2) {
                if (Move.canMove(gameState, i, currentRoll)) {
                    movablePieces.add(i);
                }
            }
        }
    }
    
    private void rollDice() {
        if (gameState.isBlackTurn) return;
        
        currentRoll = gameState.rollSticks();
        rollLabel.setText("Roll: " + currentRoll);
        
        boolean canPlay = Move.canPlay(gameState, currentRoll);
        
        if (!canPlay) {
            statusLabel.setText("No valid moves available - press 'Skip Turn'");
            skipButton.setEnabled(true);
        } else {
            statusLabel.setText("Select a piece to move " + currentRoll + " squares");
            skipButton.setEnabled(false);
            
            updateMovablePieces();
            boardPanel.repaint();
        }
        
        rollButton.setEnabled(false);
    }
    
    private void skipTurn() {
        if (gameState.isBlackTurn) return;
        
        boolean canPlay = Move.canPlay(gameState, currentRoll);
        
        if (!canPlay || currentRoll == 0) {
            statusLabel.setText("Turn skipped - opponent's turn");
            gameState.isBlackTurn = !gameState.isBlackTurn;
            selectedPiece = -1;
            targetSquare = -1;
            currentRoll = 0;
            movablePieces.clear();
            rollButton.setEnabled(true);
            skipButton.setEnabled(false);
            updateDisplay();
            
            if (gameState.isBlackTurn) {
                startComputerTurn();
            }
        } else {
            statusLabel.setText("You have valid moves - cannot skip turn");
        }
    }
    
    private void handleBoardClick(int x, int y) {
        if (currentRoll == 0 || rollButton.isEnabled() || gameState.isBlackTurn) return;
        
        for (int i = 0; i < 31; i++) {
            Point pos = squarePositions[i];
            if (x >= pos.x && x <= pos.x + SQUARE_SIZE &&
                y >= pos.y && y <= pos.y + SQUARE_SIZE) {
                
                if (selectedPiece == -1) {
                    if (i < 30) {
                        if (Move.canMove(gameState, i, currentRoll)) {
                            selectedPiece = i;
                            
                            if (shouldExitToSquare31(i, currentRoll)) {
                                targetSquare = 30;
                            } else {
                                targetSquare = Rules.RulesFunction(gameState, i, i + currentRoll, currentRoll);
                                if (targetSquare >= 30) targetSquare = 30;
                            }
                            
                            statusLabel.setText("Click on target square " + (targetSquare+1) + " to confirm move");
                            boardPanel.repaint();
                        } else {
                            int piece = gameState.board[i];
                            boolean isPlayersPiece = (!gameState.isBlackTurn && piece == 2);
                            
                            if (isPlayersPiece) {
                                statusLabel.setText("Piece cannot move to " + currentRoll + " squares");
                            } else {
                                statusLabel.setText("Select your own piece to move");
                            }
                        }
                    }
                } else {
                    if (i < 30 && i != selectedPiece) {
                        int piece = gameState.board[i];
                        boolean isPlayersPiece = (!gameState.isBlackTurn && piece == 2);
                        
                        if (isPlayersPiece && Move.canMove(gameState, i, currentRoll)) {
                            selectedPiece = i;
                            
                            if (shouldExitToSquare31(i, currentRoll)) {
                                targetSquare = 30;
                            } else {
                                targetSquare = Rules.RulesFunction(gameState, i, i + currentRoll, currentRoll);
                                if (targetSquare >= 30) targetSquare = 30;
                            }
                            
                            statusLabel.setText("Click on target square " + (targetSquare+1) + " to confirm move");
                            boardPanel.repaint();
                            return;
                        }
                    }
                    
                    if (i == targetSquare || 
                        (i == selectedPiece + currentRoll && targetSquare == -1) ||
                        Rules.RulesFunction(gameState, selectedPiece, selectedPiece + currentRoll, currentRoll) == i ||
                        (i == 30 && shouldExitToSquare31(selectedPiece, currentRoll))) {
                        
                        boolean pieceExited = false;
                        
                        if (i == 30 || shouldExitToSquare31(selectedPiece, currentRoll)) {
                            movePieceToExit(selectedPiece);
                            pieceExited = true;
                        } else {
                            Move.updateBoard(gameState, selectedPiece, currentRoll);
                        }
                        
                        selectedPiece = -1;
                        targetSquare = -1;
                        currentRoll = 0;
                        movablePieces.clear();
                        
                        updateOutPanels();
                        updateDisplay();
                        
                        rollButton.setEnabled(true);
                        skipButton.setEnabled(false);
                        
                        if (gameState.isWinner()) {
                            showWinnerList();
                        } else if (pieceExited) {
                            statusLabel.setText("Piece exited! " + (gameState.isBlackTurn ? "White" : "Black") + "'s turn");
                        }
                        
                        if (gameState.isBlackTurn) {
                            startComputerTurn();
                        }
                    } else if (i == selectedPiece) {
                        selectedPiece = -1;
                        targetSquare = -1;
                        statusLabel.setText("Selection canceled. Select a piece to move");
                        boardPanel.repaint();
                    } else {
                        statusLabel.setText("Click on the target square or another movable piece");
                    }
                }
                break;
            }
        }
    }
    
    private void startComputerTurn() {
        rollButton.setEnabled(false);
        skipButton.setEnabled(false);
        
        updateDisplay();
        
        currentRoll = gameState.rollSticks();
        rollLabel.setText("Roll: " + currentRoll);
        statusLabel.setText("Computer rolled: " + currentRoll);
        
        boardPanel.repaint();
        
        Timer delayTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Move.canPlay(gameState, currentRoll)) {
                    statusLabel.setText("Computer has no valid moves. Skipping turn...");
                    gameState.isBlackTurn = false;
                    currentRoll = 0;
                    updateDisplay();
                    rollButton.setEnabled(true);
                    ((Timer)e.getSource()).stop();
                    return;
                }
                
                statusLabel.setText("Computer is calculating move...");
                boardPanel.repaint();
                
                Timer calcTimer = new Timer(300, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int depth = 3;
                        int[] bestMove = Expectiminimax.getBestMove(gameState, depth, currentRoll);
                        
                        if (bestMove == null) {
                            statusLabel.setText("Computer has no valid moves. Skipping turn.");
                            gameState.isBlackTurn = false;
                            currentRoll = 0;
                            updateDisplay();
                            rollButton.setEnabled(true);
                        } else {
                            statusLabel.setText("Computer moves from: " + (bestMove[0] + 1) +
                                             " to: " + (bestMove[1] + 1));
                            
                            Timer moveTimer = new Timer(500, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    Move.updateBoard(gameState, bestMove[0], currentRoll);
                                    currentRoll = 0;
                                    updateOutPanels();
                                    updateDisplay();
                                    
                                    if (gameState.isWinner()) {
                                        showWinnerList();
                                    } else {
                                        statusLabel.setText("Computer's turn finished - your turn");
                                        rollButton.setEnabled(true);
                                    }
                                    
                                    ((Timer)e.getSource()).stop();
                                }
                            });
                            
                            moveTimer.setRepeats(false);
                            moveTimer.start();
                        }
                        
                        ((Timer)e.getSource()).stop();
                    }
                });
                
                calcTimer.setRepeats(false);
                calcTimer.start();
                
                ((Timer)e.getSource()).stop();
            }
        });
        
        delayTimer.setRepeats(false);
        delayTimer.start();
    }
    
    private boolean shouldExitToSquare31(int fromSquare, int roll) {
        if (fromSquare == 27 || fromSquare == 28 || fromSquare == 29) {
            if (fromSquare == 27 && roll == 3) return true;
            if (fromSquare == 28 && roll == 2) return true;
            if (fromSquare == 29) return true;
        }
        return false;
    }
    
    private void movePieceToExit(int pieceIndex) {
        int piece = gameState.board[pieceIndex];
        if (piece == 1) {
            gameState.blackOut++;
        } else {
            gameState.whiteOut++;
        }
        gameState.board[pieceIndex] = 0;
        
        gameState.isBlackTurn = !gameState.isBlackTurn;
    }
    
    private void updateOutPanels() {
        if (blackOutPanel != null) blackOutPanel.repaint();
        if (whiteOutPanel != null) whiteOutPanel.repaint();
    }
    
    private void updateDisplay() {
        turnLabel.setText("Turn: " + (gameState.isBlackTurn ? "Black (Computer)" : "White (Human)"));
        turnLabel.setForeground(gameState.isBlackTurn ? Color.green : Color.green);
        rollLabel.setText("Roll: " + currentRoll);
        
        if (currentRoll == 0) {
            statusLabel.setText(gameState.isBlackTurn ? 
                "Computer's turn" : 
                "Press 'Roll Sticks' to start your turn");
        }
        
        updateOutPanels();
        boardPanel.repaint();
    }
    
    private void showWinnerList() {
        String winner = (gameState.blackOut == 7) ? "Black (Computer)" : "White (Human)";
        String message = "Congratulations! Winner is: " + winner + "\n\n";
        message += "Pieces out:\n";
        message += "Black: " + gameState.blackOut + " / White: " + gameState.whiteOut;
        
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        
        int option = JOptionPane.showConfirmDialog(this, "Do you want to play a new game?", 
                                                  "New Game", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            showStartList();
            dispose();
        } else {
            System.exit(0);
        }
    }
}