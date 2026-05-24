public class InspectEmiWidget {
    public static void main(String[] args) throws Exception {
        for (String name : new String[]{
                "dev.emi.emi.api.widget.Widget",
                "dev.emi.emi.api.widget.ButtonWidget"
        }) {
            Class<?> c = Class.forName(name);
            System.out.println("CLASS " + c.getName());
            for (var m : c.getDeclaredMethods()) {
                System.out.println(m.toGenericString());
            }
            System.out.println();
        }
    }
}
