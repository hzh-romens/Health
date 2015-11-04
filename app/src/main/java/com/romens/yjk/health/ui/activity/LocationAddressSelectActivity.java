package com.romens.yjk.health.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.LocationAddressDao;
import com.romens.yjk.health.db.entity.LocationAddressEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siery on 15/11/4.
 */
public class LocationAddressSelectActivity extends ListSelectActivity {
    public static final String ARGUMENTS_KEY_ADDRESS_DEEP = "address_deep";
    private int addressDeep;
    private int selectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        addressDeep = intent.getIntExtra(ARGUMENTS_KEY_ADDRESS_DEEP, 0);
    }

    private void openNextAddressDeep(int position) {
        selectedPosition = position;
        String name = nameList.get(position);
        String value = valueList.get(position);
        LocationAddressDao dao = DBInterface.instance().openReadableDb().getLocationAddressDao();
        List<LocationAddressEntity> addressData = dao.queryBuilder()
                .where(LocationAddressDao.Properties.ParentId.eq(value))
                .orderDesc(LocationAddressDao.Properties.Key)
                .list();
        if (addressData == null || addressData.size() <= 0) {
            String[] names = new String[addressDeep + 1];
            names[addressDeep] = name;
            String[] values = new String[addressDeep + 1];
            values[addressDeep] = value;
            Intent data = new Intent();
            data.putExtra(RESULT_KEY_SELECTED_NAME, names);
            data.putExtra(RESULT_KEY_SELECTED_VALUE, values);
            setResult(RESULT_OK, data);
            finish();
        } else {
            ArrayList<String> names = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();
            for (LocationAddressEntity entity :
                    addressData) {
                names.add(entity.getName());
                values.add(entity.getKey());
            }
            Intent intent = new Intent(this, LocationAddressSelectActivity.class);
            final int targetDeep = addressDeep+1;
            intent.putExtra(LocationAddressSelectActivity.ARGUMENTS_KEY_ADDRESS_DEEP, targetDeep);
            intent.putExtra(LocationAddressSelectActivity.ARGUMENTS_KEY_TITLE, name);
            intent.putExtra(LocationAddressSelectActivity.ARGUMENTS_KEY_NAME_LIST, names);
            intent.putExtra(LocationAddressSelectActivity.ARGUMENTS_KEY_VALUE_LIST, values);
            intent.putExtra(LocationAddressSelectActivity.ARGUMENTS_KEY_ONLY_SELECT, false);
            startActivityForResult(intent, 0);
        }
    }


    @Override
    protected void onItemSelected(int position) {
        openNextAddressDeep(position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String[] names = data.getStringArrayExtra(RESULT_KEY_SELECTED_NAME);
                String[] values = data.getStringArrayExtra(RESULT_KEY_SELECTED_VALUE);
                names[addressDeep] = nameList.get(selectedPosition);
                values[addressDeep] = valueList.get(selectedPosition);
                Intent result = new Intent();
                result.putExtra(RESULT_KEY_SELECTED_NAME, names);
                result.putExtra(RESULT_KEY_SELECTED_VALUE, values);
                setResult(RESULT_OK, result);
                finish();
            }
        }
    }
}
