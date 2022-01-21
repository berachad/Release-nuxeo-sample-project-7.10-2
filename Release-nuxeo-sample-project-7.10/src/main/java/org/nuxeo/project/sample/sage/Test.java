package org.nuxeo.project.sample.sage;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Test {

	public static void main(String[] args) throws JSONException {
		// TODO Auto-generated method stub

		String parsed = "{\r\n" + 
				"  \"myHashMap\": {\r\n" + 
				"    \"$items\": {\r\n" + 
				"      \"myArrayList\": [\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"Checking (1010)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/d1fb0ee9040b4569a77891dc4ce745b2\",\r\n" + 
				"            \"id\": \"d1fb0ee9040b4569a77891dc4ce745b2\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"Cash (1020)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/3e6f9f0dca8d46cebfba21529323ac45\",\r\n" + 
				"            \"id\": \"3e6f9f0dca8d46cebfba21529323ac45\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"Accounts Receivable (1100)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/9be46bec36d911eaae730ee73a5c6c6b\",\r\n" + 
				"            \"id\": \"9be46bec36d911eaae730ee73a5c6c6b\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"Allowance for Doubtful Account (1150)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/9be48e1336d911eaae730ee73a5c6c6b\",\r\n" + 
				"            \"id\": \"9be48e1336d911eaae730ee73a5c6c6b\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"Inventory (1200)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/9be4926836d911eaae730ee73a5c6c6b\",\r\n" + 
				"            \"id\": \"9be4926836d911eaae730ee73a5c6c6b\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"Prepaid Expenses (1400)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/9be4965c36d911eaae730ee73a5c6c6b\",\r\n" + 
				"            \"id\": \"9be4965c36d911eaae730ee73a5c6c6b\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"Property and Equipment (1500)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/9be49a4236d911eaae730ee73a5c6c6b\",\r\n" + 
				"            \"id\": \"9be49a4236d911eaae730ee73a5c6c6b\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"Accum. Depreciation - Prop\\u0026Eqt (1900)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/9be49e0536d911eaae730ee73a5c6c6b\",\r\n" + 
				"            \"id\": \"9be49e0536d911eaae730ee73a5c6c6b\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"Accounts Payable (2000)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/9be4a1b236d911eaae730ee73a5c6c6b\",\r\n" + 
				"            \"id\": \"9be4a1b236d911eaae730ee73a5c6c6b\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"Sales Tax Payable (2310)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/9be4a58036d911eaae730ee73a5c6c6b\",\r\n" + 
				"            \"id\": \"9be4a58036d911eaae730ee73a5c6c6b\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"Deductions Payable (2320)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/9be4a93636d911eaae730ee73a5c6c6b\",\r\n" + 
				"            \"id\": \"9be4a93636d911eaae730ee73a5c6c6b\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"Federal Payroll Taxes Payable (2330)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/9be4acf036d911eaae730ee73a5c6c6b\",\r\n" + 
				"            \"id\": \"9be4acf036d911eaae730ee73a5c6c6b\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"FUTA Payable (2340)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/9be4b08336d911eaae730ee73a5c6c6b\",\r\n" + 
				"            \"id\": \"9be4b08336d911eaae730ee73a5c6c6b\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"State Payroll Taxes Payable (2350)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/9be4b40536d911eaae730ee73a5c6c6b\",\r\n" + 
				"            \"id\": \"9be4b40536d911eaae730ee73a5c6c6b\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"SUTA Payable (2360)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/9be4b78136d911eaae730ee73a5c6c6b\",\r\n" + 
				"            \"id\": \"9be4b78136d911eaae730ee73a5c6c6b\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"Local Taxes Payable (2370)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/9be4bb1736d911eaae730ee73a5c6c6b\",\r\n" + 
				"            \"id\": \"9be4bb1736d911eaae730ee73a5c6c6b\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"Income Taxes Payable (2380)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/9be4bee736d911eaae730ee73a5c6c6b\",\r\n" + 
				"            \"id\": \"9be4bee736d911eaae730ee73a5c6c6b\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"Customer Deposits (2400)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/9be4c26436d911eaae730ee73a5c6c6b\",\r\n" + 
				"            \"id\": \"9be4c26436d911eaae730ee73a5c6c6b\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"Current Portion Long-Term Debt (2500)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/9be4c6ca36d911eaae730ee73a5c6c6b\",\r\n" + 
				"            \"id\": \"9be4c6ca36d911eaae730ee73a5c6c6b\"\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"          \"myHashMap\": {\r\n" + 
				"            \"displayed_as\": \"Long Term Debt-Noncurrent (2700)\",\r\n" + 
				"            \"$path\": \"/ledger_accounts/9be4ca5d36d911eaae730ee73a5c6c6b\",\r\n" + 
				"            \"id\": \"9be4ca5d36d911eaae730ee73a5c6c6b\"\r\n" + 
				"          }\r\n" + 
				"        }\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    \"$back\": {},\r\n" + 
				"    \"$total\": 62,\r\n" + 
				"    \"$itemsPerPage\": 20,\r\n" + 
				"    \"$next\": \"/ledger_accounts?page\\u003d2\\u0026items_per_page\\u003d20\",\r\n" + 
				"    \"$page\": 1\r\n" + 
				"  }\r\n" + 
				"}";
		JSONObject jsonResponse = new JSONObject(parsed);
		JSONArray  jsonArrayResponse = jsonResponse.getJSONObject("myHashMap").getJSONObject("$items").getJSONArray("myArrayList");
		for (int i = 0; i < jsonArrayResponse.length(); i++) {
            JSONObject objects = jsonArrayResponse.getJSONObject(i);
            Iterator<String> key = objects.keys();
            while (key.hasNext()) {
                String k = key.next().toString();
                JSONObject objects1 =  objects.getJSONObject("myHashMap");
                System.out.println("............");
                System.out.println(objects1);
                Iterator<String> key1 = objects1.keys();
                while (key1.hasNext()) {
                	String k1 = key1.next().toString();
                	System.out.println("Key : " + k1 + ", value : " + objects1.getString(k1));
                }
                System.out.println("............");
                //System.out.println("Key : " + k + ", value : " + objects.getString(k));
                if(k.equals("$path")) {
                	System.out.println("Key : " + k + ", value : " + objects.getString(k));
                    //list.add(objects.getString(k));
                	if(objects.getString(k).equals("Camera")) {
                System.out.println("Key : " + k + ", value : " + objects.getString(k));
                    }
                }
            }
	}

}
}
