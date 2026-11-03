package eng1.c1group12.world;

public record WorldData(
        int tile_res,
        int pixels_per_unit,
        int x,
        int y
){
    @Override
    public String toString() {
        return "x: " + x + " y: " + y + " tile_res: " + tile_res + " pixels_per_unit: " + pixels_per_unit;
    }
}
