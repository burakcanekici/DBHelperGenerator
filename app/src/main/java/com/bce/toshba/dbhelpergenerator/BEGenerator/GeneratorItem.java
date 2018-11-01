package com.bce.toshba.dbhelpergenerator.BEGenerator;

public class GeneratorItem {
    protected String column_name;
    protected String data_type;

    protected GeneratorItem(String pColumnName, String pDataType){
        this.column_name = pColumnName;
        this.data_type = pDataType;
    }
}
