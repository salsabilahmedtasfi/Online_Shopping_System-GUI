import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

// ════════════════════════════════════════════════════════════
//  ENTRY POINT
// ════════════════════════════════════════════════════════════
public class MainGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            applyGlobalUI();
            new AppFrame().setVisible(true);
        });
    }

    static void applyGlobalUI() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        UIManager.put("Table.background",           Theme.CARD);
        UIManager.put("Table.foreground",           Theme.TEXT);
        UIManager.put("Table.selectionBackground",  Theme.ACCENT);
        UIManager.put("Table.selectionForeground",  Color.WHITE);
        UIManager.put("Table.gridColor",            Theme.BORDER);
        UIManager.put("TableHeader.background",     Theme.BG);
        UIManager.put("TableHeader.foreground",     Theme.MUTED);
        UIManager.put("ScrollPane.background",      Theme.BG);
        UIManager.put("ScrollBar.thumb",            Theme.BORDER);
        UIManager.put("ScrollBar.track",            Theme.BG);
        UIManager.put("OptionPane.background",      Theme.CARD);
        UIManager.put("OptionPane.messageForeground", Theme.TEXT);
        UIManager.put("Panel.background",           Theme.BG);
    }
}

// ════════════════════════════════════════════════════════════
//  THEME
// ════════════════════════════════════════════════════════════
class Theme {
    static final Color BG      = new Color(0x0D0F18);
    static final Color CARD    = new Color(0x171A27);
    static final Color INPUT   = new Color(0x21243A);
    static final Color BORDER  = new Color(0x2A2E4A);
    static final Color ACCENT  = new Color(0x7B6FFF);
    static final Color ACCENT2 = new Color(0xFF6B6B);
    static final Color SUCCESS = new Color(0x3DD68C);
    static final Color WARN    = new Color(0xFFBF5C);
    static final Color TEXT    = new Color(0xECEDF5);
    static final Color MUTED   = new Color(0x7A7E9A);

    static final Font TITLE = new Font("SansSerif", Font.BOLD,  22);
    static final Font HEAD  = new Font("SansSerif", Font.BOLD,  13);
    static final Font BODY  = new Font("SansSerif", Font.PLAIN, 13);
    static final Font SMALL = new Font("SansSerif", Font.PLAIN, 11);
    static final Font MONO  = new Font("Monospaced", Font.PLAIN, 12);
}

// ════════════════════════════════════════════════════════════
//  REUSABLE WIDGETS
// ════════════════════════════════════════════════════════════
class RoundPanel extends JPanel {
    private final int r; private final Color bg;
    RoundPanel(int r, Color bg) { this.r=r; this.bg=bg; setOpaque(false); }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2=(Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),r,r));
        g2.dispose(); super.paintComponent(g);
    }
}

class Btn extends JButton {
    private final Color base; private boolean hot;
    Btn(String txt, Color base) {
        super(txt); this.base=base;
        setOpaque(false); setContentAreaFilled(false); setBorderPainted(false);
        setFocusPainted(false); setForeground(Color.WHITE); setFont(Theme.HEAD);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(9,22,9,22));
        addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){hot=true;repaint();}
            public void mouseExited(MouseEvent e){hot=false;repaint();}
        });
    }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2=(Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(hot?base.brighter():base);
        g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),10,10));
        g2.dispose(); super.paintComponent(g);
    }
}

class TField extends JTextField {
    private final String ph;
    TField(String ph) {
        super(18); this.ph=ph; reset();
        setFont(Theme.BODY); setBackground(Theme.INPUT); setCaretColor(Theme.ACCENT);
        setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.BORDER,1,true),
            BorderFactory.createEmptyBorder(8,12,8,12)));
        setOpaque(true);
        addFocusListener(new FocusAdapter(){
            public void focusGained(FocusEvent e){if(getText().equals(ph)){setText("");setForeground(Theme.TEXT);}}
            public void focusLost(FocusEvent e){if(getText().isBlank()) reset();}
        });
    }
    private void reset(){setText(ph);setForeground(Theme.MUTED);}
    String val(){String t=getText().trim();return t.equals(ph)?"":t;}
}

class PField extends JPasswordField {
    PField() {
        setFont(Theme.BODY); setBackground(Theme.INPUT);
        setForeground(Theme.TEXT); setCaretColor(Theme.ACCENT);
        setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.BORDER,1,true),
            BorderFactory.createEmptyBorder(8,12,8,12)));
        setOpaque(true);
    }
}

class Lbl extends JLabel {
    Lbl(String t,Font f,Color c){super(t);setFont(f);setForeground(c);}
}

// Table factory
class TFactory {
    static JTable make(DefaultTableModel m) {
        JTable t=new JTable(m);
        t.setBackground(Theme.CARD); t.setForeground(Theme.TEXT);
        t.setSelectionBackground(Theme.ACCENT); t.setSelectionForeground(Color.WHITE);
        t.setGridColor(Theme.BORDER); t.setRowHeight(34); t.setFont(Theme.BODY);
        t.setShowHorizontalLines(true); t.setShowVerticalLines(false);
        t.setIntercellSpacing(new Dimension(0,1));
        t.setFillsViewportHeight(true);
        JTableHeader h=t.getTableHeader();
        h.setBackground(Theme.BG); h.setForeground(Theme.MUTED); h.setFont(Theme.SMALL);
        h.setBorder(new MatteBorder(0,0,1,0,Theme.BORDER));
        h.setReorderingAllowed(false);
        // center price column (index 2)
        DefaultTableCellRenderer center=new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        if (m.getColumnCount()>2) t.getColumnModel().getColumn(2).setCellRenderer(center);
        return t;
    }
    static JScrollPane scroll(JTable t) {
        JScrollPane s=new JScrollPane(t);
        s.setBorder(BorderFactory.createEmptyBorder());
        s.getViewport().setBackground(Theme.CARD);
        s.setBackground(Theme.BG);
        return s;
    }
}

// ════════════════════════════════════════════════════════════
//  APP STATE  (single source of truth)
// ════════════════════════════════════════════════════════════
class AppState {
    final AuthService auth = new AuthService();
    final Shop        shop = new Shop();
    final Cart        cart = new Cart();
    User currentUser;
}

// ════════════════════════════════════════════════════════════
//  APP FRAME
// ════════════════════════════════════════════════════════════
class AppFrame extends JFrame {
    final AppState   state = new AppState();
    final CardLayout cards = new CardLayout();
    final JPanel     root  = new JPanel(cards);
    ShopView shopView;

    AppFrame() {
        super("🛒  ShopApp");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1020, 700); setMinimumSize(new Dimension(820,560));
        setLocationRelativeTo(null);
        root.setBackground(Theme.BG);
        shopView = new ShopView(this);
        root.add(new LoginView(this), "LOGIN");
        root.add(shopView,            "SHOP");
        add(root);
    }

    void goShop(User u) {
        state.currentUser=u; shopView.onLogin(); cards.show(root,"SHOP");
    }
    void goLogin() {
        state.currentUser=null; state.cart.clear();
        shopView.onLogout(); cards.show(root,"LOGIN");
    }
}

// ════════════════════════════════════════════════════════════
//  LOGIN VIEW
// ════════════════════════════════════════════════════════════
class LoginView extends JPanel {
    private final AppFrame app;
    private final TField   userF = new TField("Username");
    private final PField   passF = new PField();
    private final Lbl      statusL = new Lbl("", Theme.SMALL, Theme.ACCENT2);

    LoginView(AppFrame app) {
        this.app=app; setBackground(Theme.BG); setLayout(new GridBagLayout()); buildUI();
    }

    private void buildUI() {
        RoundPanel card = new RoundPanel(20, Theme.CARD);
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(44,52,44,52));
        card.setPreferredSize(new Dimension(400,480));

        Lbl logo = new Lbl("🛒 ShopApp", Theme.TITLE, Theme.ACCENT);
        logo.setAlignmentX(CENTER_ALIGNMENT);
        Lbl sub = new Lbl("Sign in or create an account", Theme.SMALL, Theme.MUTED);
        sub.setAlignmentX(CENTER_ALIGNMENT);
        statusL.setAlignmentX(CENTER_ALIGNMENT);

        Btn loginBtn = new Btn("Login",    Theme.ACCENT);
        Btn regBtn   = new Btn("Register", Theme.BORDER.brighter());
        for (Btn b : new Btn[]{loginBtn,regBtn}) {
            b.setAlignmentX(CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));
        }
        for (JComponent f : new JComponent[]{userF,passF}) {
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
            f.setAlignmentX(LEFT_ALIGNMENT);
        }

        loginBtn.addActionListener(e -> doLogin());
        regBtn.addActionListener(e -> doRegister());
        passF.addActionListener(e -> doLogin());

        card.add(logo);      card.add(Box.createVerticalStrut(6));
        card.add(sub);       card.add(Box.createVerticalStrut(32));
        card.add(lbl("Username")); card.add(Box.createVerticalStrut(5));
        card.add(userF);     card.add(Box.createVerticalStrut(16));
        card.add(lbl("Password")); card.add(Box.createVerticalStrut(5));
        card.add(passF);     card.add(Box.createVerticalStrut(26));
        card.add(loginBtn);  card.add(Box.createVerticalStrut(10));
        card.add(regBtn);    card.add(Box.createVerticalStrut(14));
        card.add(statusL);
        add(card);
    }

    private Lbl lbl(String t){Lbl l=new Lbl(t,Theme.SMALL,Theme.MUTED);l.setAlignmentX(LEFT_ALIGNMENT);return l;}

    private void doLogin() {
        String u=userF.val(), p=new String(passF.getPassword()).trim();
        if(u.isEmpty()||p.isEmpty()){status("Fill in both fields.",Theme.WARN);return;}
        User user=app.state.auth.login(u,p);
        if(user==null) status("Invalid credentials.",Theme.ACCENT2);
        else { passF.setText(""); status("",Theme.SUCCESS); app.goShop(user); }
    }
    private void doRegister() {
        String u=userF.val(), p=new String(passF.getPassword()).trim();
        if(u.isEmpty()||p.isEmpty()){status("Fill in both fields.",Theme.WARN);return;}
        app.state.auth.register(u,p);
        status("Registered! You can now log in.",Theme.SUCCESS);
    }
    private void status(String m,Color c){statusL.setText(m);statusL.setForeground(c);}
}

// ════════════════════════════════════════════════════════════
//  SHOP VIEW  (top bar + tabbed content)
// ════════════════════════════════════════════════════════════
class ShopView extends JPanel {
    final AppFrame app;
    private final Lbl greetL = new Lbl("", Theme.HEAD, Theme.TEXT);
    private ProductsTab productsTab;
    private CartTab     cartTab;
    private OrdersTab   ordersTab;
    private AdminTab    adminTab;
    private JTabbedPane tabs;

    ShopView(AppFrame app) {
        this.app=app; setBackground(Theme.BG); setLayout(new BorderLayout()); buildUI();
    }

    private void buildUI() {
        // Top bar
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.CARD);
        bar.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0,0,1,0,Theme.BORDER),
            BorderFactory.createEmptyBorder(14,24,14,24)));
        bar.add(new Lbl("🛒  ShopApp", Theme.TITLE, Theme.ACCENT), BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,0));
        right.setBackground(Theme.CARD);
        right.add(greetL);
        Btn logout = new Btn("Logout", Theme.ACCENT2);
        logout.addActionListener(e -> app.goLogin());
        right.add(logout);
        bar.add(right, BorderLayout.EAST);
        add(bar, BorderLayout.NORTH);

        // Tabs
        productsTab = new ProductsTab(app, this);
        cartTab     = new CartTab(app, this);
        ordersTab   = new OrdersTab(app);
        adminTab    = new AdminTab(app);

        tabs = new JTabbedPane();
        tabs.setBackground(Theme.BG);
        tabs.setFont(Theme.HEAD);
        tabs.addTab("  Products  ", productsTab);
        tabs.addTab("  Cart      ", cartTab);
        tabs.addTab("  Orders    ", ordersTab);
        tabs.addTab("  Admin     ", adminTab);
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent()==cartTab)   cartTab.refresh();
            if (tabs.getSelectedComponent()==ordersTab) ordersTab.refresh();
        });
        add(tabs, BorderLayout.CENTER);
    }

    void onLogin()  { greetL.setText("Hi, "+app.state.currentUser.getUsername()+" 👋"); productsTab.refresh(); cartTab.refresh(); ordersTab.refresh(); }
    void onLogout() { greetL.setText(""); }
    void switchCart()   { tabs.setSelectedComponent(cartTab);   cartTab.refresh(); }
    void switchOrders() { tabs.setSelectedComponent(ordersTab); ordersTab.refresh(); }
}

// ════════════════════════════════════════════════════════════
//  PRODUCTS TAB
// ════════════════════════════════════════════════════════════
class ProductsTab extends JPanel {
    private final AppFrame  app;
    private final ShopView  shopView;
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID","Product","Price"},0){
        public boolean isCellEditable(int r,int c){return false;}
    };

    ProductsTab(AppFrame app, ShopView shopView) {
        this.app=app; this.shopView=shopView;
        setBackground(Theme.BG); setLayout(new BorderLayout()); build();
    }

    private void build() {
        JTable table = TFactory.make(model);
        // fix column widths
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(2).setMaxWidth(100);

        Btn addBtn = new Btn("Add to Cart  +", Theme.ACCENT);
        addBtn.addActionListener(e -> {
            int row=table.getSelectedRow();
            if(row<0){msg("Select a product first."); return;}
            int id=(int)model.getValueAt(row,0);
            Product p=app.state.shop.getProduct(id);
            if(p!=null){ app.state.cart.addProduct(p); msg("\""+p.getName()+"\" added!"); }
        });

        Btn viewCartBtn = new Btn("View Cart →", Theme.INPUT.brighter());
        viewCartBtn.addActionListener(e -> shopView.switchCart());

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,10));
        bot.setBackground(Theme.BG);
        bot.setBorder(new MatteBorder(1,0,0,0,Theme.BORDER));
        bot.add(viewCartBtn); bot.add(addBtn);

        add(TFactory.scroll(table), BorderLayout.CENTER);
        add(bot, BorderLayout.SOUTH);
    }

    void refresh() {
        model.setRowCount(0);
        for(Product p:app.state.shop.getProducts())
            model.addRow(new Object[]{p.getId(),p.getName(),String.format("$%.2f",p.getPrice())});
    }

    private void msg(String m){ JOptionPane.showMessageDialog(this,m,"ShopApp",JOptionPane.INFORMATION_MESSAGE); }
}

// ════════════════════════════════════════════════════════════
//  CART TAB
// ════════════════════════════════════════════════════════════
class CartTab extends JPanel {
    private final AppFrame app;
    private final ShopView shopView;
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID","Product","Price"},0){
        public boolean isCellEditable(int r,int c){return false;}
    };
    private final Lbl totalL = new Lbl("Total: $0.00", Theme.HEAD, Theme.SUCCESS);
    private JTable table;

    CartTab(AppFrame app, ShopView shopView) {
        this.app=app; this.shopView=shopView;
        setBackground(Theme.BG); setLayout(new BorderLayout()); build();
    }

    private void build() {
        table = TFactory.make(model);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(2).setMaxWidth(100);

        Btn removeBtn   = new Btn("Remove Selected", Theme.ACCENT2);
        Btn checkoutBtn = new Btn("Checkout  →", Theme.SUCCESS);

        removeBtn.addActionListener(e -> {
            int row=table.getSelectedRow();
            if(row<0) return;
            int id=(int)model.getValueAt(row,0);
            app.state.cart.removeProduct(id); refresh();
        });
        checkoutBtn.addActionListener(e -> {
            if(app.state.cart.getItems().isEmpty()){
                JOptionPane.showMessageDialog(this,"Your cart is empty!","Cart",JOptionPane.WARNING_MESSAGE); return;
            }
            new CheckoutDialog(app, shopView, this).setVisible(true);
        });

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,10));
        bot.setBackground(Theme.BG);
        bot.setBorder(new MatteBorder(1,0,0,0,Theme.BORDER));
        bot.add(totalL); bot.add(Box.createHorizontalStrut(10));
        bot.add(removeBtn); bot.add(checkoutBtn);

        add(TFactory.scroll(table), BorderLayout.CENTER);
        add(bot, BorderLayout.SOUTH);
    }

    void refresh() {
        model.setRowCount(0);
        for(Product p:app.state.cart.getItems())
            model.addRow(new Object[]{p.getId(),p.getName(),String.format("$%.2f",p.getPrice())});
        totalL.setText(String.format("Total: $%.2f",app.state.cart.getTotal()));
    }
}

// ════════════════════════════════════════════════════════════
//  CHECKOUT DIALOG
// ════════════════════════════════════════════════════════════
class CheckoutDialog extends JDialog {
    private final AppFrame app;
    private final ShopView shopView;
    private final CartTab  cartTab;

    private final JComboBox<String> methodBox = new JComboBox<>(new String[]{"Credit Card","PayPal","Cash on Delivery"});
    private final CardLayout detailCards = new CardLayout();
    private final JPanel     detailPanel = new JPanel(detailCards);

    // CC fields
    private final TField ccNum    = new TField("Card Number (digits)");
    private final TField ccHolder = new TField("Cardholder Name");
    private final TField ccExp    = new TField("MM/YY");
    private final TField ccCvv    = new TField("CVV");
    // PP
    private final TField ppEmail  = new TField("PayPal Email");
    // COD
    private final TField codAddr  = new TField("Delivery Address");

    CheckoutDialog(AppFrame app, ShopView shopView, CartTab cartTab) {
        super((Frame)null,"Checkout",true);
        this.app=app; this.shopView=shopView; this.cartTab=cartTab;
        setSize(500,580); setLocationRelativeTo(app);
        getContentPane().setBackground(Theme.CARD);
        build();
    }

    private void build() {
        JPanel root=new JPanel(); root.setBackground(Theme.CARD);
        root.setLayout(new BoxLayout(root,BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(30,38,30,38));

        // Title
        Lbl title=new Lbl("Checkout",Theme.TITLE,Theme.TEXT); title.setAlignmentX(LEFT_ALIGNMENT);

        // Summary box
        JTextArea summary=new JTextArea(); summary.setEditable(false);
        summary.setFont(Theme.MONO); summary.setBackground(Theme.INPUT); summary.setForeground(Theme.TEXT);
        summary.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.BORDER,1,true), BorderFactory.createEmptyBorder(10,12,10,12)));
        summary.setAlignmentX(LEFT_ALIGNMENT); summary.setMaximumSize(new Dimension(Integer.MAX_VALUE,130));
        StringBuilder sb=new StringBuilder();
        for(Product p:app.state.cart.getItems())
            sb.append(String.format("  %-24s $%.2f%n",p.getName(),p.getPrice()));
        sb.append(String.format("%n  %-24s $%.2f","TOTAL",app.state.cart.getTotal()));
        summary.setText(sb.toString());

        // Method picker
        Lbl methodLbl=new Lbl("Payment Method",Theme.SMALL,Theme.MUTED); methodLbl.setAlignmentX(LEFT_ALIGNMENT);
        methodBox.setBackground(Theme.INPUT); methodBox.setForeground(Theme.TEXT); methodBox.setFont(Theme.BODY);
        methodBox.setMaximumSize(new Dimension(Integer.MAX_VALUE,38)); methodBox.setAlignmentX(LEFT_ALIGNMENT);
        methodBox.addActionListener(e->detailCards.show(detailPanel,(String)methodBox.getSelectedItem()));

        // Detail panels
        detailPanel.setBackground(Theme.CARD); detailPanel.setAlignmentX(LEFT_ALIGNMENT);
        detailPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,170));
        detailPanel.add(ccPanel(),"Credit Card");
        detailPanel.add(ppPanel(),"PayPal");
        detailPanel.add(codPanel(),"Cash on Delivery");

        // Place button
        Btn placeBtn=new Btn("Place Order  →",Theme.ACCENT);
        placeBtn.setAlignmentX(LEFT_ALIGNMENT); placeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE,44));
        placeBtn.addActionListener(e->placeOrder());

        root.add(title);                        root.add(vgap(16));
        root.add(new Lbl("Order Summary",Theme.SMALL,Theme.MUTED)); root.add(vgap(6));
        root.add(summary);                      root.add(vgap(20));
        root.add(methodLbl);                    root.add(vgap(6));
        root.add(methodBox);                    root.add(vgap(14));
        root.add(detailPanel);                  root.add(vgap(20));
        root.add(placeBtn);

        JScrollPane sp=new JScrollPane(root);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Theme.CARD);
        setContentPane(sp);
    }

    private JPanel ccPanel() {
        JPanel p=colPanel();
        for(TField f:new TField[]{ccNum,ccHolder,ccExp,ccCvv}){ f.setMaximumSize(new Dimension(Integer.MAX_VALUE,38)); f.setAlignmentX(LEFT_ALIGNMENT); p.add(f); p.add(vgap(8)); }
        return p;
    }
    private JPanel ppPanel() {
        JPanel p=colPanel(); ppEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE,38)); ppEmail.setAlignmentX(LEFT_ALIGNMENT); p.add(ppEmail); return p;
    }
    private JPanel codPanel() {
        JPanel p=colPanel(); codAddr.setMaximumSize(new Dimension(Integer.MAX_VALUE,38)); codAddr.setAlignmentX(LEFT_ALIGNMENT); p.add(codAddr); return p;
    }
    private JPanel colPanel(){JPanel p=new JPanel();p.setBackground(Theme.CARD);p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));return p;}
    private Component vgap(int h){return Box.createVerticalStrut(h);}

    private void placeOrder() {
        String m=(String)methodBox.getSelectedItem();
        PaymentMethod pm;
        switch(m) {
            case "Credit Card": pm=new CreditCardPayment(ccNum.val(),ccHolder.val(),ccExp.val(),ccCvv.val()); break;
            case "PayPal":      pm=new PayPalPayment(ppEmail.val()); break;
            default:            pm=new CashOnDelivery(codAddr.val());
        }
        Order order=new Order(app.state.cart.getItems(),app.state.cart.getTotal(),pm);
        if(order.isPaid()) {
            app.state.currentUser.addOrder(order);
            app.state.cart.clear();
            cartTab.refresh();
            dispose();
            JOptionPane.showMessageDialog(app,
                "<html><b>✅ Order placed!</b><br>Payment: "+pm.getLabel()+"</html>",
                "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);
            shopView.switchOrders();
        } else {
            JOptionPane.showMessageDialog(this,
                "❌ Payment failed. Check your details and try again.",
                "Payment Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

// ════════════════════════════════════════════════════════════
//  ORDERS TAB
// ════════════════════════════════════════════════════════════
class OrdersTab extends JPanel {
    private final AppFrame app;
    private final JPanel   list = new JPanel();

    OrdersTab(AppFrame app) {
        this.app=app; setBackground(Theme.BG); setLayout(new BorderLayout());
        list.setLayout(new BoxLayout(list,BoxLayout.Y_AXIS));
        list.setBackground(Theme.BG);
        list.setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        JScrollPane sp=new JScrollPane(list);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Theme.BG);
        add(sp,BorderLayout.CENTER);
    }

    void refresh() {
        list.removeAll();
        List<Order> orders=app.state.currentUser==null?Collections.emptyList():app.state.currentUser.getOrders();
        if(orders.isEmpty()) {
            Lbl e=new Lbl("No orders yet.",Theme.BODY,Theme.MUTED); e.setAlignmentX(LEFT_ALIGNMENT);
            list.add(Box.createVerticalStrut(30)); list.add(e);
        } else {
            for(int i=orders.size()-1;i>=0;i--) { list.add(orderCard(orders.get(i))); list.add(Box.createVerticalStrut(12)); }
        }
        list.revalidate(); list.repaint();
    }

    private JComponent orderCard(Order o) {
        RoundPanel card=new RoundPanel(14,Theme.CARD);
        card.setLayout(new BorderLayout(0,8));
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.BORDER,1),
            BorderFactory.createEmptyBorder(14,18,14,18)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE,999));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JPanel top=new JPanel(new BorderLayout()); top.setOpaque(false);
        top.add(new Lbl(o.getPlacedAt(),Theme.SMALL,Theme.MUTED),BorderLayout.WEST);
        JPanel tr=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); tr.setOpaque(false);
        Color sc=o.isPaid()?Theme.SUCCESS:Theme.ACCENT2;
        tr.add(new Lbl(o.isPaid()?"● PAID":"● FAILED",Theme.SMALL,sc));
        tr.add(new Lbl(String.format("$%.2f",o.getTotal()),Theme.HEAD,Theme.TEXT));
        top.add(tr,BorderLayout.EAST);

        JTextArea items=new JTextArea(); items.setEditable(false); items.setOpaque(false);
        items.setFont(Theme.MONO); items.setForeground(Theme.MUTED);
        for(Product p:o.getProducts()) items.append(String.format("  %-24s $%.2f%n",p.getName(),p.getPrice()));

        Lbl payL=new Lbl("via "+o.getPaymentLabel(),Theme.SMALL,Theme.ACCENT);

        card.add(top,BorderLayout.NORTH);
        card.add(items,BorderLayout.CENTER);
        card.add(payL,BorderLayout.SOUTH);
        return card;
    }
}

// ════════════════════════════════════════════════════════════
//  ADMIN TAB
// ════════════════════════════════════════════════════════════
class AdminTab extends JPanel {
    private final AppFrame app;
    private final TField idF=new TField("ID (number)"),nameF=new TField("Product Name"),priceF=new TField("Price e.g. 49.99");
    private final Lbl status=new Lbl("",Theme.SMALL,Theme.SUCCESS);

    AdminTab(AppFrame app) {
        this.app=app; setBackground(Theme.BG); setLayout(new GridBagLayout()); build();
    }

    private void build() {
        RoundPanel card=new RoundPanel(18,Theme.CARD);
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(36,46,36,46));
        card.setPreferredSize(new Dimension(420,340));

        Btn addBtn=new Btn("Add Product",Theme.ACCENT);
        addBtn.setAlignmentX(LEFT_ALIGNMENT); addBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));
        status.setAlignmentX(LEFT_ALIGNMENT);
        addBtn.addActionListener(e->{
            try {
                int id=Integer.parseInt(idF.val()); String name=nameF.val(); double price=Double.parseDouble(priceF.val());
                if(name.isEmpty()) throw new IllegalArgumentException();
                app.state.shop.addProduct(new Product(id,name,price));
                status.setForeground(Theme.SUCCESS); status.setText("✅ \""+name+"\" added to shop.");
            } catch(Exception ex){ status.setForeground(Theme.ACCENT2); status.setText("❌ Invalid input. Check ID and Price."); }
        });

        Lbl title=new Lbl("Add New Product",Theme.TITLE,Theme.TEXT); title.setAlignmentX(LEFT_ALIGNMENT);
        for(TField f:new TField[]{idF,nameF,priceF}){ f.setMaximumSize(new Dimension(Integer.MAX_VALUE,38)); f.setAlignmentX(LEFT_ALIGNMENT); }

        card.add(title);               card.add(vg(24));
        card.add(lbl("Product ID"));   card.add(vg(5));  card.add(idF);    card.add(vg(14));
        card.add(lbl("Name"));         card.add(vg(5));  card.add(nameF);  card.add(vg(14));
        card.add(lbl("Price ($)"));    card.add(vg(5));  card.add(priceF); card.add(vg(22));
        card.add(addBtn);              card.add(vg(12));
        card.add(status);
        add(card);
    }
    private Lbl lbl(String t){Lbl l=new Lbl(t,Theme.SMALL,Theme.MUTED);l.setAlignmentX(LEFT_ALIGNMENT);return l;}
    private Component vg(int h){return Box.createVerticalStrut(h);}
}

// ════════════════════════════════════════════════════════════
//  BACKEND  (all in one file for easy compilation)
// ════════════════════════════════════════════════════════════
class AuthService {
    private final List<User> users=new ArrayList<>();
    public void register(String u,String p){ users.add(new User(u,p)); System.out.println("Registered: "+u); }
    public User login(String u,String p){
        return users.stream().filter(x->x.getUsername().equals(u)&&x.getPassword().equals(p)).findFirst().orElse(null);
    }
}

class Shop {
    private final List<Product> products=new ArrayList<>();
    public Shop(){
        products.add(new Product(1,"Laptop",      800.00));
        products.add(new Product(2,"Smartphone",  500.00));
        products.add(new Product(3,"Headphones",  100.00));
        products.add(new Product(4,"Mechanical Keyboard", 75.00));
        products.add(new Product(5,"4K Monitor",  350.00));
        products.add(new Product(6,"Webcam",       60.00));
    }
    public List<Product> getProducts(){return Collections.unmodifiableList(products);}
    public Product getProduct(int id){return products.stream().filter(p->p.getId()==id).findFirst().orElse(null);}
    public void addProduct(Product p){products.add(p);}
}

class Cart {
    private final List<Product> items=new ArrayList<>();
    public void addProduct(Product p){items.add(p);}
    public void removeProduct(int id){items.removeIf(p->p.getId()==id);}
    public double getTotal(){return items.stream().mapToDouble(Product::getPrice).sum();}
    public List<Product> getItems(){return Collections.unmodifiableList(items);}
    public void clear(){items.clear();}
}

class Product {
    private final int id; private final String name; private final double price;
    public Product(int id,String n,double p){this.id=id;name=n;price=p;}
    public int getId(){return id;} public String getName(){return name;} public double getPrice(){return price;}
    public void display(){System.out.printf("%d. %s - $%.2f%n",id,name,price);}
}

class User {
    private final String username,password;
    private final List<Order> orders=new ArrayList<>();
    public User(String u,String p){username=u;password=p;}
    public String getUsername(){return username;} public String getPassword(){return password;}
    public void addOrder(Order o){orders.add(o);}
    public List<Order> getOrders(){return Collections.unmodifiableList(orders);}
}

class Order {
    private static final DateTimeFormatter FMT=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final List<Product> products; private final double total;
    private final PaymentMethod pm; private final String placedAt; private final boolean paid;
    public Order(List<Product> products,double total,PaymentMethod pm){
        this.products=new ArrayList<>(products); this.total=total; this.pm=pm;
        this.placedAt=LocalDateTime.now().format(FMT); this.paid=pm.pay(total);
    }
    public boolean isPaid(){return paid;} public double getTotal(){return total;}
    public String getPlacedAt(){return placedAt;} public String getPaymentLabel(){return pm.getLabel();}
    public List<Product> getProducts(){return products;}
    public void showOrder(){System.out.printf("[%s] %s $%.2f via %s%n",placedAt,paid?"PAID":"FAILED",total,pm.getLabel());}
}

// Payment system
interface PaymentMethod { boolean pay(double amount); String getLabel(); }

class CreditCardPayment implements PaymentMethod {
    private final String num,holder,expiry,cvv;
    public CreditCardPayment(String n,String h,String e,String c){num=n.replaceAll("\\s+","");holder=h;expiry=e;cvv=c;}
    public boolean pay(double amount){
        if(!luhn()){System.out.println("Invalid card.");return false;}
        if(!exp()){System.out.println("Expired.");return false;}
        if(!cvvOk()){System.out.println("Bad CVV.");return false;}
        System.out.printf("CC charged $%.2f%n",amount);return true;
    }
    public String getLabel(){return "Credit Card (****"+num.substring(Math.max(0,num.length()-4))+")";}
    private boolean luhn(){
        if(num==null||!num.matches("\\d{13,19}"))return false;
        int s=0;boolean a=false;
        for(int i=num.length()-1;i>=0;i--){int n=num.charAt(i)-'0';if(a){n*=2;if(n>9)n-=9;}s+=n;a=!a;}
        return s%10==0;
    }
    private boolean exp(){
        if(expiry==null||!expiry.matches("\\d{2}/\\d{2}"))return false;
        int m=Integer.parseInt(expiry.substring(0,2)),y=2000+Integer.parseInt(expiry.substring(3));
        if(m<1||m>12)return false;
        return !java.time.YearMonth.of(y,m).isBefore(java.time.YearMonth.now());
    }
    private boolean cvvOk(){return cvv!=null&&cvv.matches("\\d{3,4}");}
}

class PayPalPayment implements PaymentMethod {
    private final String email;
    public PayPalPayment(String e){email=e;}
    public boolean pay(double amount){
        if(email==null||!email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")){System.out.println("Bad email.");return false;}
        System.out.printf("PayPal $%.2f%n",amount);return true;
    }
    public String getLabel(){return "PayPal ("+email+")";}
}

class CashOnDelivery implements PaymentMethod {
    private final String address;
    public CashOnDelivery(String a){address=a;}
    public boolean pay(double amount){
        if(address==null||address.isBlank()){System.out.println("No address.");return false;}
        System.out.printf("COD $%.2f%n",amount);return true;
    }
    public String getLabel(){return "Cash on Delivery ("+address+")";}
}
