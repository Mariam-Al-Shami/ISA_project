import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.awt.image.BufferedImage;

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
    private int targetSquare = -1; // Square to move to (for green highlight)
    
    // Colors for the interface
    private static final Color BOARD_COLOR = new Color(210, 180, 140); // Wood color
    private static final Color LIGHT_SQUARE = new Color(240, 217, 181); // Light beige
    private static final Color DARK_SQUARE = new Color(181, 136, 99);   // Dark brown
    private static final Color BLACK_PIECE = new Color(50, 50, 50);
    private static final Color WHITE_PIECE = new Color(230, 230, 230);
    private static final Color HIGHLIGHT_COLOR = new Color(255, 215, 0, 100);
    private static final Color TARGET_HIGHLIGHT = new Color(0, 255, 0, 70); // Green transparent for target
    private static final Color EXIT_SQUARE_COLOR = new Color(100, 238, 140, 180); // Light green for exit square
    
    // Dimensions
    private static final int SQUARE_SIZE = 70;
    private static final int PIECE_SIZE = 35;
    private static final int MARGIN = 75;
    
    // Square positions (following U-shaped path)
    private Point[] squarePositions = new Point[31]; // Now 31 squares including exit
    
    // Maps for storing images and labels
    private HashMap<Integer, ImageIcon> squareIcons;
    
    // Panels for out pieces
    private JPanel blackOutPanel;
    private JPanel whiteOutPanel;
    
    public SenetGUI() {
        showStartDialog();
    }
    
    private void showStartDialog() {
        // Show dialog to choose who starts
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
        
        // Initialize game state based on choice
        gameState = new State();
        if (choice == 1) { // Computer starts
            gameState.isBlackTurn = true;
        } else { // Human starts (default)
            gameState.isBlackTurn = false;
        }
        
        initializeSquarePositions();
        initializeSquareIconsAndLabels();
        setupGUI();
        updateDisplay();
    }
    
    private void initializeSquarePositions() {
        // Top row (1-10) left to right
        for (int i = 0; i < 10; i++) {
            squarePositions[i] = new Point(
                MARGIN + i * SQUARE_SIZE,
                MARGIN
            );
        }
        
        // Middle row (11-20) right to left
        for (int i = 10; i < 20; i++) {
            squarePositions[i] = new Point(
                MARGIN + (19 - i) * SQUARE_SIZE,
                MARGIN + SQUARE_SIZE
            );
        }
        
        // Bottom row (21-30) left to right
        for (int i = 20; i < 30; i++) {
            squarePositions[i] = new Point(
                MARGIN + (i - 20) * SQUARE_SIZE,
                MARGIN + 2 * SQUARE_SIZE
            );
        }
        
        // Exit square (31) - placed to the right of square 30
        squarePositions[30] = new Point(
            MARGIN + 10 * SQUARE_SIZE + 20, // Right of square 30
            MARGIN + 2 * SQUARE_SIZE
        );
    }
    
    private void initializeSquareIconsAndLabels() {
        squareIcons = new HashMap<>();
        
        // List of special squares and corresponding image files
        int[] specialSquares = {14, 25, 26, 27, 28, 29}; // 15, 26, 27, 28, 29, 30
        String[] imageFiles = {
            "rebirth.png",    // ☀️ for rebirth
            "barrier.png",    // 🧱 for barrier
            "water.png",      // 💧 for water
            "three.png",      // ③ for three
            "two.png",        // ② for two
            "freedom.png"     // 🕊️ for freedom
        };
        
        for (int i = 0; i < specialSquares.length; i++) {
            try {
                // Load image from file
                ImageIcon icon = loadImageIcon("images/" + imageFiles[i], 50, 50);
                if (icon != null) {
                    squareIcons.put(specialSquares[i], icon);
                }
            } catch (Exception e) {
                System.out.println("Error loading image: " + imageFiles[i]);
            }
        }
    }
    
    private ImageIcon loadImageIcon(String path, int width, int height) {
        try {
            // Try to load image from file
            File file = new File(path);
            if (file.exists()) {
                Image image = ImageIO.read(file);
                Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
            
            // Try to load from resources
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
        
        // Top information panel
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
        skipButton.setEnabled(false); // Initially disabled
        
        infoPanel.add(turnLabel);
        infoPanel.add(rollButton);
        infoPanel.add(rollLabel);
        infoPanel.add(skipButton);
        infoPanel.add(statusLabel);
        
        // Main game board
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
                drawPieces(g);
                drawSpecialIcons(g);
                drawExitSquare(g);
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
                handleBoardClick(e.getX(), e.getY());
            }
        });
        
        // Out pieces panel
        JPanel outPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        outPanel.setBackground(new Color(70, 50, 30));
        
        // Create out panels with custom drawing
        blackOutPanel = createOutPanel("out Black (Computer)", BLACK_PIECE, true);
        whiteOutPanel = createOutPanel("out White (Human)", WHITE_PIECE, false);
        
        outPanel.add(whiteOutPanel);
        outPanel.add(blackOutPanel);
        
        // Add components
        add(infoPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(outPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JPanel createOutPanel(String title, Color pieceColor, boolean isBlack) {
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
                // Clear the panel first
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw the pieces
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
        
        // Draw squares with alternating colors (like chessboard)
        for (int i = 0; i < 30; i++) {
            Point pos = squarePositions[i];
            
            // Determine square color based on position
            int row = i / 10;  // 0, 1, 2 for the three rows
            int col = i % 10;  // 0-9 for columns
            
            // For middle row (11-20), direction is reversed
            if (row == 1) {
                col = 9 - col;
            }
            
            boolean isLight = ((row + col) % 2 == 0);
            g2d.setColor(isLight ? LIGHT_SQUARE : DARK_SQUARE);
            g2d.fillRect(pos.x, pos.y, SQUARE_SIZE, SQUARE_SIZE);
            
            // Square border
            g2d.setColor(Color.BLACK);
            g2d.drawRect(pos.x, pos.y, SQUARE_SIZE, SQUARE_SIZE);
            
            // Square number
            g2d.setFont(new Font("Arial", Font.PLAIN, 13));
            String num = String.valueOf(i + 1);
            FontMetrics fm = g2d.getFontMetrics();
            int numX = pos.x + (SQUARE_SIZE - fm.stringWidth(num)) / 2;
            int numY = pos.y + SQUARE_SIZE - 8;
            
            // Make text color contrast with background
            g2d.setColor(isLight ? Color.BLACK : Color.WHITE);
            g2d.drawString(num, numX, numY);
        }
    }
    
    private void drawExitSquare(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Point pos = squarePositions[30];
        
        // Draw exit square (green)
        g2d.setColor(EXIT_SQUARE_COLOR);
        g2d.fillRect(pos.x, pos.y, SQUARE_SIZE, SQUARE_SIZE);
        
        // Square border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(pos.x, pos.y, SQUARE_SIZE, SQUARE_SIZE);
        
        // Exit label
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        String label = "Quit";
        FontMetrics fm = g2d.getFontMetrics();
        int labelX = pos.x + (SQUARE_SIZE - fm.stringWidth(label)) / 2;
        int labelY = pos.y + SQUARE_SIZE / 2 + 5;
        g2d.setColor(Color.BLACK);
        g2d.drawString(label, labelX, labelY);
        
        // Square number
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
            
            // Draw image label
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
        
        // 3D circular disc
        GradientPaint gradient = new GradientPaint(
            x, y, color.brighter(),
            x + PIECE_SIZE, y + PIECE_SIZE, color.darker()
        );
        g2d.setPaint(gradient);
        g2d.fillOval(x, y, PIECE_SIZE, PIECE_SIZE);
        
        // Piece border
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x, y, PIECE_SIZE, PIECE_SIZE);
        
        // 3D shading
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.drawOval(x + 1, y + 1, PIECE_SIZE - 2, PIECE_SIZE - 2);
        
        // Piece identifier letter
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
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(pos.x + 3, pos.y + 3, SQUARE_SIZE - 6, SQUARE_SIZE - 6);
        
        // Flash effect
        g2d.setColor(new Color(255, 215, 0, 70));
        g2d.fillRect(pos.x + 3, pos.y + 3, SQUARE_SIZE - 6, SQUARE_SIZE - 6);
    }
    
    private void drawTargetHighlight(Graphics g, int squareIndex) {
        if (squareIndex < 0 || squareIndex >= 31) return;
        
        Graphics2D g2d = (Graphics2D) g;
        Point pos = squarePositions[squareIndex];
        
        // Green transparent highlight for target square
        g2d.setColor(TARGET_HIGHLIGHT);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(pos.x + 3, pos.y + 3, SQUARE_SIZE - 6, SQUARE_SIZE - 6);
        
        // Green fill effect
        g2d.setColor(new Color(0, 255, 0, 40));
        g2d.fillRect(pos.x + 3, pos.y + 3, SQUARE_SIZE - 6, SQUARE_SIZE - 6);
    }
    
    private void rollDice() {
        currentRoll = gameState.rollSticks();
        rollLabel.setText("Roll: " + currentRoll);
        statusLabel.setText("Select a piece to move " + currentRoll + " squares");
        rollButton.setEnabled(false);
        
        // Check if there are valid moves
        boolean canPlay = Move.canPlay(gameState, currentRoll);
        
        if (!canPlay) {
            statusLabel.setText("No valid moves available - press 'Skip Turn'");
            skipButton.setEnabled(true);
        } else {
            statusLabel.setText("Select a piece to move " + currentRoll + " squares");
            skipButton.setEnabled(false);
        }
    }
    
    private void skipTurn() {
        // Only allow skip when there are no valid moves
        boolean canPlay = Move.canPlay(gameState, currentRoll);
        
        if (!canPlay || currentRoll == 0) {
            statusLabel.setText("Turn skipped - opponent's turn");
            gameState.isBlackTurn = !gameState.isBlackTurn;
            selectedPiece = -1;
            targetSquare = -1;
            currentRoll = 0;
            rollButton.setEnabled(true);
            skipButton.setEnabled(false);
            updateDisplay();
        } else {
            statusLabel.setText("You have valid moves - cannot skip turn");
        }
    }
    
    private void handleBoardClick(int x, int y) {
        if (currentRoll == 0 || rollButton.isEnabled()) return;
        
        // Find clicked square
        for (int i = 0; i < 31; i++) {
            Point pos = squarePositions[i];
            if (x >= pos.x && x <= pos.x + SQUARE_SIZE &&
                y >= pos.y && y <= pos.y + SQUARE_SIZE) {
                
                if (selectedPiece == -1) {
                    // Selecting a piece to move
                    if (i < 30) {
                        // Check if this piece can be moved
                        if (Move.canMove(gameState, i, currentRoll)) {
                            selectedPiece = i;
                            
                            // Calculate target square
                            if (shouldExitToSquare31(i, currentRoll)) {
                                targetSquare = 30; // Exit square
                            } else {
                                targetSquare = Rules.RulesFunction(gameState, i, i + currentRoll, currentRoll);
                                if (targetSquare >= 30) targetSquare = 30;
                            }
                            
                            statusLabel.setText("Click on target square or click another piece");
                            boardPanel.repaint();
                        } else {
                            // Check if player clicked on their own piece that cannot move
                            int piece = gameState.board[i];
                            boolean isPlayersPiece = (gameState.isBlackTurn && piece == 1) || (!gameState.isBlackTurn && piece == 2);
                            
                            if (isPlayersPiece) {
                                statusLabel.setText("This piece cannot move " + currentRoll + " squares");
                            } else {
                                statusLabel.setText("Select your own piece to move");
                            }
                        }
                    }

                } else {
                    // If clicking on a different piece, change selection
                    if (i < 30 && i != selectedPiece) {
                        // Check if the clicked piece can be moved
                        if (Move.canMove(gameState, i, currentRoll)) {
                            selectedPiece = i;
                            
                            // Calculate new target square
                            if (shouldExitToSquare31(i, currentRoll)) {
                                targetSquare = 30; // Exit square
                            } else {
                                targetSquare = Rules.RulesFunction(gameState, i, i + currentRoll, currentRoll);
                                if (targetSquare >= 30) targetSquare = 30;
                                
                            }
                            
                            statusLabel.setText("Click on target square or click another piece");
                            boardPanel.repaint();
                            return;
                        }

                    }

                    // Confirm movement (clicking on target square)
                    if (i == targetSquare || 
                        (i == selectedPiece + currentRoll && targetSquare == -1) ||
                        Rules.RulesFunction(gameState, selectedPiece, selectedPiece + currentRoll, currentRoll) == i ||
                        (i == 30 && shouldExitToSquare31(selectedPiece, currentRoll))) {
                        
                        boolean pieceExited = false;
                        
                        // Check if moving to exit square (31)
                        if (i == 30 || shouldExitToSquare31(selectedPiece, currentRoll)) {
                            movePieceToExit(selectedPiece);
                            pieceExited = true;
                        } else {
                            Move.updateBoard(gameState, selectedPiece, currentRoll);
                        }
                        
                        selectedPiece = -1;
                        targetSquare = -1;
                        currentRoll = 0;
                        
                        // Update out panels immediately
                        updateOutPanels();
                        updateDisplay();
                        
                        rollButton.setEnabled(true);
                        skipButton.setEnabled(false);

                        if (gameState.isWinner()) {
                            showWinnerDialog();
                        } else if (pieceExited) {
                            // Show message when piece exits
                            statusLabel.setText("Piece exited! " + (gameState.isBlackTurn ? "White" : "Black") + "'s turn");
                        }

                    } else if (i == selectedPiece) {
                        // Clicking on the same piece - deselect it
                        selectedPiece = -1;
                        targetSquare = -1;
                        statusLabel.setText("Selection canceled, select a piece to move");
                        boardPanel.repaint();
                    } else {
                        // Show message about where to click
                        if (targetSquare != -1) {
                            if (targetSquare == 30) {
                                statusLabel.setText("Click on the Exit square (31) to confirm move");
                            } else {
                                statusLabel.setText("Click on square " + (targetSquare+1) + " to confirm move");
                            }
                        }
                    }

                }
                break;
            }
        }
    }
    
    private boolean shouldExitToSquare31(int fromSquare, int roll) {
        // Check if piece is on special exit squares (28, 29, 30)
        if (fromSquare == 27 || fromSquare == 28 || fromSquare == 29) {
            // Check if roll allows exit
            if (fromSquare == 27 && roll == 3) return true; // Three Truths
            if (fromSquare == 28 && roll == 2) return true; // Re-Atom
            if (fromSquare == 29) return true; // Horus (any roll)
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
        
        // Switch turn
        gameState.isBlackTurn = !gameState.isBlackTurn;
    }
    
    private void updateOutPanels() {
        // Force repaint of out panels
        if (blackOutPanel != null) {
            blackOutPanel.repaint();
        }
        if (whiteOutPanel != null) {
            whiteOutPanel.repaint();
        }
    }
    
    private void updateDisplay() {
        turnLabel.setText("Turn: " + (gameState.isBlackTurn ? "Black (Computer)" : "White (Human)"));
        turnLabel.setForeground(gameState.isBlackTurn ? Color.green : Color.green);
        rollLabel.setText("Roll: " + currentRoll);
        
        if (currentRoll == 0) {
            statusLabel.setText("Press 'Roll Sticks' to start your turn");
        }
        
        // Update out panels
        updateOutPanels();
        
        // Repaint board
        boardPanel.repaint();
    }
    
    private void showWinnerDialog() {
        String winner = (gameState.blackOut == 7) ? "Black (Computer)" : "White (Human)";
        String message = "Congratulations! Winner is: " + winner + "\n\n";
        message += "Pieces out:\n";
        message += "Black: " + gameState.blackOut + " / White: " + gameState.whiteOut;
        
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        
        int option = JOptionPane.showConfirmDialog(this, "Do you want to play a new game?", 
                                                  "New Game", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            // Reset game
            showStartDialog();
            dispose(); // Close current window
        } else {
            System.exit(0);
        }
    
    }
}