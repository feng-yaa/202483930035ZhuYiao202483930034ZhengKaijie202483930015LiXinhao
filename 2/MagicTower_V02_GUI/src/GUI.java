import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class GUI {
	GameData gameData;
	JFrame f;
	private JDialog menuDialog;
	JLabel[][] b;
	private Element menuRootElement;
	private JTextArea menuDisplayArea;
	private JLabel gameModeLabel;

	GUI(GameData gameData) {
		this.gameData = gameData;
		f = new JFrame("Magic Tower");

		// 1. 先添加模式图标（底层）
		gameModeLabel = new JLabel();
		int iconX = 10; // 左边
		int iconY = 10; // 上边
		gameModeLabel.setBounds(iconX, iconY, 200, 70); // 恢复正常尺寸

		// 移除调试设置，恢复正常外观
		gameModeLabel.setOpaque(false); // 不设置背景色
		gameModeLabel.setBorder(null);   // 移除边框

		f.add(gameModeLabel);

		// 2. 后添加地图格子（上层）
		b = new JLabel[gameData.H][gameData.W];
		for (int i = 0; i < gameData.H; i++) {
			for (int j = 0; j < gameData.W; j++) {
				b[i][j] = new JLabel();
				b[i][j].setBounds(j * 100, i * 100, 100, 100);

				// 移除地图格子的调试设置
				b[i][j].setOpaque(false); // 恢复正常
				b[i][j].setBorder(null);  // 移除边框

				f.add(b[i][j]);
			}
		}

		f.setSize(gameData.H * 100 + 10, gameData.W * 100 + 40);
		f.setLayout(null);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 加载菜单配置
		loadMenuFromXML("Menu.XML");

		// 初始刷新
		refreshGUI();
	}

	private void updateGameModeIcon(JLabel label) {
		String imagePath;
		if(!gameData.bleeding){
			 imagePath = "";
			return;
		}else{
			 imagePath = "./Steel_Soul.png";
		}


		File file = new File(imagePath);
		if (!file.exists()) {

			// 备用显示：只在图片不存在时显示文字
			label.setText(gameData.bleeding ? "STEEL" : "NORMAL");
			label.setForeground(Color.WHITE);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setVerticalAlignment(SwingConstants.CENTER);
			return;
		}

		ImageIcon icon = new ImageIcon(imagePath);
		if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
			Image scaledImage = icon.getImage().getScaledInstance(200, 70, Image.SCALE_SMOOTH);
			label.setIcon(new ImageIcon(scaledImage));
			label.setText(null); // 确保移除文字
		} else {
			// 图片加载失败时的备用显示
			label.setText("ICON");
			label.setForeground(Color.WHITE);
			label.setHorizontalAlignment(SwingConstants.CENTER);
		}

		// 设置提示文本
		if (gameData.bleeding) {
			label.setToolTipText("Steel Soul Mode\n--Your hardest experience");
		} else {
			label.setToolTipText("Normal Mode");
		}
	}

	// 从XML加载菜单配置
	private void loadMenuFromXML(String filePath) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new File(filePath));
			document.getDocumentElement().normalize();
			menuRootElement = document.getDocumentElement();
			removeTextNodes(menuRootElement);
		} catch (Exception e) {
			System.out.println("菜单加载失败: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void removeTextNodes(Node node) {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node child = childNodes.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				String textContent = child.getTextContent().trim();
				if (textContent.isEmpty()) {
					node.removeChild(child);
					i--;
				}
			} else if (child.getNodeType() == Node.ELEMENT_NODE) {
				removeTextNodes(child);
			}
		}
	}

	// 创建菜单对话框
	private void createMenuDialog(Element currentElement) {
		if (menuDialog != null) {
			menuDialog.dispose();
		}

		menuDialog = new JDialog(f, "游戏菜单", true);
		menuDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		menuDialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeMenu();
			}

			@Override
			public void windowOpened(WindowEvent e) {
				menuDialog.requestFocusInWindow();
			}
		});

		menuDialog.setSize(500, 600);
		menuDialog.setLocationRelativeTo(f);
		menuDialog.setResizable(false);

		JPanel menuPanel = new JPanel();
		menuPanel.setLayout(new BorderLayout());
		menuPanel.setBackground(new Color(20, 20, 40));
		menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		menuDisplayArea = new JTextArea();
		menuDisplayArea.setEditable(false);
		menuDisplayArea.setBackground(new Color(30, 30, 50));
		menuDisplayArea.setForeground(Color.WHITE);
		Font chineseFont = new Font("宋体", Font.PLAIN, 16);
		menuDisplayArea.setFont(chineseFont);

		String menuText = generateMenuText(currentElement);
		menuDisplayArea.setText(menuText);

		JScrollPane scrollPane = new JScrollPane(menuDisplayArea);
		menuPanel.add(scrollPane, BorderLayout.CENTER);

		JLabel hintLabel = new JLabel("请输入数字选择菜单项 (ESC返回游戏)");
		hintLabel.setForeground(Color.YELLOW);
		hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
		menuPanel.add(hintLabel, BorderLayout.SOUTH);

		menuDialog.add(menuPanel);
		menuDialog.setFocusable(true);
		setupMenuKeyListener(currentElement);

		menuDialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				menuDialog.requestFocusInWindow();
			}
		});
	}

	private String generateMenuText(Element element) {
		StringBuilder sb = new StringBuilder();
		String title = element.getAttribute("title");
		sb.append("=========================================\n");
		sb.append("             ").append(title).append("             \n");
		sb.append("=========================================\n\n");

		NodeList childElements = element.getChildNodes();
		for (int i = 0; i < childElements.getLength(); i++) {
			Element childElement = (Element) childElements.item(i);
			String itemTitle = childElement.getAttribute("title");
			sb.append(" ").append(i + 1).append(". ").append(itemTitle).append("\n\n");
		}

		sb.append(" 0. Return to game\n");
		return sb.toString();
	}

	private void setupMenuKeyListener(final Element currentElement) {
		menuDialog.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (c >= '0' && c <= '9') {
					int inputNum = Character.getNumericValue(c);
					handleMenuInput(inputNum, currentElement);
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					closeMenu();
				}
			}
		});
	}

	private void handleMenuInput(int inputNum, Element currentElement) {
		if (inputNum == 0) {
			closeMenu();
			return;
		}

		NodeList childElements = currentElement.getChildNodes();
		if (inputNum > 0 && inputNum <= childElements.getLength()) {
			Element selectedElement = (Element) childElements.item(inputNum - 1);
			String isFunction = selectedElement.getAttribute("isFunction");

			if ("No".equals(isFunction)) {
				createMenuDialog(selectedElement);
				menuDialog.setVisible(true);
			} else {
				handleMenuFunction(isFunction);
				closeMenu();
			}
		}
	}

	private void handleMenuFunction(String functionName) {
		switch (functionName) {
			case "restartGame":
				gameData.readMapFromFile("Map.in");
				JOptionPane.showMessageDialog(menuDialog, "游戏已重新开始！");
				refreshGUI();
				break;
			case "saveGame":
				saveGame();
				break;
			case "loadGame":
				loadGame();
				break;
			case "quitGame":
				int choice = JOptionPane.showConfirmDialog(menuDialog,
						"确定要退出游戏吗？", "确认退出", JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
				break;
			case "SSM":
				gameData.bleeding = true;
				refreshGUI();
				break;
			case "NM":
				gameData.bleeding = false;
				refreshGUI();
				break;
			default:
				JOptionPane.showMessageDialog(menuDialog, "功能暂未实现: " + functionName);
		}
	}

	public void refreshGUI() {
		// 更新地图
		for (int i = 0; i < gameData.H; i++) {
			for (int j = 0; j < gameData.W; j++) {
				Image scaledImage = chooseImage(gameData.map[gameData.currentLevel][i][j]);
				b[i][j].setIcon(new ImageIcon(scaledImage));
			}
		}

		// 更新图标
		updateGameModeIcon(gameModeLabel);

		// 强制重绘
		f.revalidate();
		f.repaint();
	}

	private static Image chooseImage(int index){
		ImageIcon[] icons = new ImageIcon[10];
		Image scaledImage;
		icons[0]= new ImageIcon("Wall.jpg");
		icons[1]= new ImageIcon("Floor.jpg");
		icons[2]= new ImageIcon("Key.jpg");
		icons[3]= new ImageIcon("Door.jpg");
		icons[4]= new ImageIcon("Stair.jpg");
		icons[5]= new ImageIcon("Exit.jpg");
		icons[6]= new ImageIcon("Hero.jpg");
		icons[7]= new ImageIcon("Potion.jpg");
		icons[8]= new ImageIcon("Monster.jpg");
		if(index>10)
			scaledImage = icons[7].getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
		else if(index<0)
			scaledImage = icons[8].getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
		else
			scaledImage = icons[index].getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
		return scaledImage;
	}

	public void showMenu() {
		createMenuDialog(menuRootElement);
		menuDialog.setVisible(true);
	}

	public void closeMenu() {
		if (menuDialog != null) {
			menuDialog.setVisible(false);
			menuDialog.dispose();
			menuDialog = null;
		}
		f.requestFocus();
	}

	public boolean isMenuVisible() {
		return menuDialog != null && menuDialog.isVisible();
	}

	private void saveGame() {
		try {
			JOptionPane.showMessageDialog(menuDialog, "游戏保存成功！");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(menuDialog, "保存失败: " + e.getMessage());
		}
	}

	private void loadGame() {
		try {
			JOptionPane.showMessageDialog(menuDialog, "游戏加载成功！");
			refreshGUI();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(menuDialog, "加载失败: " + e.getMessage());
		}
	}
}