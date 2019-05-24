package aplikasiku.padi.tnfaid.pantaupadi.Model;

/**
 * id = 1
 * jenis_tanaman = cibogo
 * warna_daun = #FFFFFFf
 * bwd_range = 1
 * solusi = tambahkan nitrogen 25%
 * pic_compare = lalalla.jpg
 */
public class Daun_Model {
    private int id;
    private String jenis_tanaman;
    private String warna_daun;
    private int bwd_range;
    private String solusi;
    private String pic_compare;

    public Daun_Model (int id, String jenis_tanaman, String warna_daun, int bwd_range, String solusi, String pic_compare)
    {
        this.id = id;
        this.jenis_tanaman = jenis_tanaman;
        this.warna_daun = warna_daun;
        this.bwd_range = bwd_range;
        this.solusi = solusi;
        this.pic_compare = pic_compare;
    }

    public int getId(){return id;}

    public void setId(int id) {this.id = id;}

    public String getJenis_tanaman() {return jenis_tanaman;}

    public void setJenis_tanaman() {this.jenis_tanaman = jenis_tanaman;}

    public String getWarna_daun() {return warna_daun;}

    public void setWarna_daun() {this.warna_daun = warna_daun;}

    public int getBwd_range() {return bwd_range;}

    public void setBwd_range() {this.bwd_range = bwd_range;}

    public String getSolusi() {return solusi;}

    public void setSolusi() { this.solusi = solusi;}

    public String getPic_compare() {return pic_compare;}

    public void setPic_compare() { this.pic_compare = pic_compare;}
}
