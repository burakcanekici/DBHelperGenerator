package com.bce.toshba.dbhelpergenerator.BEGenerator;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bce.toshba.dbhelpergenerator.BEGenerator.GeneratorItem;

import java.util.ArrayList;
import java.util.Locale;

public class Generator {
    private SQLiteOpenHelper openHelper;
    private String databaseName;
    private String tableName;
    private String autoGenerateEntityName;
    private ArrayList<GeneratorItem> columns;
    private ArrayList<String> primaryKeys;

    public Generator(SQLiteOpenHelper pOpenHelper, String pDatabaseName){
        this.openHelper = pOpenHelper;
        this.databaseName = pDatabaseName;
    }

    public String StartGenerate(String pTableName){
        String message = "";
        tableName = pTableName;

        autoGenerateEntityName = tableName.toUpperCase(Locale.ENGLISH);

        GetAllColumnsAndDataTypes();
        GetPrimaryKeysColumnName();


        String contentOfDBHelperClass = AppendToContentOfDBHelperClassString();

        return message;
    }

    // to get all columns and their data types from selected table and primary key(s) of selected table.
    public void GetAllColumnsAndDataTypes(){
        columns = new ArrayList<GeneratorItem>();

        SQLiteDatabase db = openHelper.getReadableDatabase();
        String sql = "SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + tableName + "'";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        if(!cursor.isAfterLast()){
            while (!cursor.isAfterLast()){
                String column_name = cursor.getString(cursor.getColumnIndex("COLUMN_NAME"));
                String data_type = cursor.getString(cursor.getColumnIndex("DATA_TYPE"));
                columns.add(new GeneratorItem(column_name, data_type));
                cursor.moveToNext();
            }
        }

        db.close();
    }
    public void GetPrimaryKeysColumnName(){
        primaryKeys = new ArrayList<String>();

        SQLiteDatabase db = openHelper.getReadableDatabase();
        String sql = "SELECT COL.COLUMN_NAME FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS TAB, INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE COL";
        sql += " WHERE COL.CONSTRAINT_NAME = TAB.CONSTRAINT_NAME AND COL.TABLE_NAME = TAB.TABLE_NAME AND CONSTRAINT_TYPE = 'PRIMARY KEY' AND COL.TABLE_NAME = '" + tableName + "'";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        if(!cursor.isAfterLast()){
            while (!cursor.isAfterLast()){
                primaryKeys.add(cursor.getString(cursor.getColumnIndex("COLUMN_NAME")));
                cursor.moveToNext();
            }
        }

        db.close();
    }


    // generate DBHelper class content
    public String AppendToContentOfDBHelperClassString(){
        String content = "";
        content += GenerateGetAllMethod(); // getList() method
        content += GenerateInsertMethod(); // insert() method

        return content;
    }

    public String GenerateGetAllMethod(){
        String content = "public ArrayList<" + autoGenerateEntityName + "> getList(){\n";
        content += "\tArrayList<\" + autoGenerateEntityName + \"> array_list = new ArrayList<" + autoGenerateEntityName + ">();\n";
        content += "\tSQLiteDatabase db = this.getReadableDatabase();\n";
        content += "\tCursor cursor =  db.rawQuery( \"select * from " + tableName + "\", null );\n";
        content += "\tcursor.moveToFirst();\n";
        content += "\twhile(cursor.isAfterLast() == false){\n";
        content += "\t\tarray_list.add(new \"" + autoGenerateEntityName + "\"(";

        for(int i=0;i<columns.size();i++){
            content += "cursor.getString(cursor.getColumnIndex(" + columns.get(i).column_name + "))";
            if(i != columns.size()-1)
                content += ",";
        }

        content += "));\n";
        content += "\t\tcursor.moveToNext();\n";
        content += "\t}\n";
        content += "\treturn array_list;\n";
        content += "}\n\n\n";

        return content;
    } // getList() method
    public String GenerateInsertMethod(){
        String content = "public boolean insert(" + autoGenerateEntityName + " param){\n";
        content += "\tSQLiteDatabase db = this.getWritableDatabase();\n";
        content += "\tContentValues contentValues = new ContentValues();\n";

        for(int i=0;i<columns.size();i++){
            if(!primaryKeys.contains(columns.get(i).column_name))
                content += "\tcontentValues.put(\"" + columns.get(i).column_name + "\", param." + columns.get(i).column_name + ");\n";
        }

        content += "\tdb.insert(\"" + tableName + "\", null, contentValues);\n";
        content += "\treturn true";
        content += "}";

        return content;
    } // insert() method
    public String GenerateUpdateMethod(){
        String content = "public boolean insert(" + autoGenerateEntityName + " param){\n";
        content += "\tSQLiteDatabase db = this.getWritableDatabase();\n";
        content += "\tContentValues contentValues = new ContentValues();\n";

        for(int i=0;i<columns.size();i++){
            if(!primaryKeys.contains(columns.get(i).column_name))
                content += "\tcontentValues.put(\"" + columns.get(i).column_name + "\", param." + columns.get(i).column_name + ");\n";
        }

        content += "\tdb.update(\"" + tableName + "\", null, contentValues);\n";
        content += "\treturn true";
        content += "}";

        return content;
    } // insert() method

}
