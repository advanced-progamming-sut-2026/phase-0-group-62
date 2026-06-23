import controller.MenuController;
import view.menu.RegisterMenu;
import view.menu.Menu;

public class Main {
    public static void main(String[] args) {
        // ۱. ساخت کنترلر (مغز متفکر)
        MenuController controller = new MenuController();

        // ۲. ساخت منوی ثبت‌نام (اتاق کار)
        Menu registerMenu = new RegisterMenu(controller);

        // ۳. اجرای منو
        registerMenu.run();
    }
}