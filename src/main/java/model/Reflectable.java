package model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;



interface Reflectable {

    static List<String> getFieldNames(Field[] fields) {
        List<String> fieldNames = new ArrayList<>();
        for (Field field : fields)
            fieldNames.add(field.getName());
        return fieldNames;
    }


}
