package com.example.wireseeker.database;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity()
public class Wire {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "table_name")
    private String wireTable;

    @ColumnInfo(name = "wire_num")
    private String wireNum;

    @ColumnInfo(name = "point")
    private String point;

    public Wire(String wireTable,String wireNum, String point) {
        this.wireTable = wireTable;
        this.wireNum = wireNum;
        this.point = point;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWireTable() {
        return wireTable;
    }

    public void setWireTable(String wireTable) {
        this.wireTable = wireTable;
    }

    public String getWireNum() {
        return wireNum;
    }

    public void setWireNum(String wireNum) {
        this.wireNum = wireNum;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}
