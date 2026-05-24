public class InspectFluidPlaceEvent {
    public static void main(String[] args) throws Exception {
        Class<?> c = Class.forName("net.neoforged.neoforge.event.level.BlockEvent$FluidPlaceBlockEvent");
        System.out.println("CLASS " + c.getName());
        for (var m : c.getDeclaredMethods()) {
            System.out.println(m.toGenericString());
        }
    }
}
