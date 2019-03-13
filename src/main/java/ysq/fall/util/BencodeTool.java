package ysq.fall.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Deprecated
public class BencodeTool {

    // Used to determine the next bencoded input type in the file.
    private final int NULL_TYPE = 0;

    private final int STRING = 1;

    private final int INTEGER = 2;

    private final int LIST = 3;

    private final int DICTIONARY = 4;

    private final int STRUCTURE_END = 5;
    
    public String getBtih(InputStream input) throws FileNotFoundException, NoSuchAlgorithmException, IOException{
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

        try (ByteArrayOutputStream output = new ByteArrayOutputStream();) {
            int data;
            while ((data = input.read()) > -1) {
                output.write(data);
            }
            Index index = new Index();
            index.index = 0;
            BencodeDictionary benDict = unbencodeDictionary(output.toByteArray(), index);
            BencodeDictionary info = (BencodeDictionary) benDict.getMap().get(new BencodeString("4:info"));
            List<Byte> bytes = info.getBytes();
            byte[] bs = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); i++) {
                bs[i] = bytes.get(i);
            }
            sha1.update(bs);
            byte[] hash = sha1.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                sb.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }
    }

    public String getMagnetLink(InputStream input) throws FileNotFoundException, NoSuchAlgorithmException, IOException {
        return "magnet:?xt=urn:btih:"+getBtih(input);
    }

    public abstract class BencodeObject {

        public abstract List<Byte> getBytes();

    }

    public class BencodeInteger extends BencodeObject {

        private List<Byte> bytes;

        public BencodeInteger() {
            super();
        }

        public BencodeInteger(List<Byte> bytes) {
            super();
            this.bytes = bytes;
        }

        @Override
        public List<Byte> getBytes() {
            return bytes;
        }

        public void setBytes(List<Byte> bytes) {
            this.bytes = bytes;
        }

    }

    public class BencodeString extends BencodeObject {

        private List<Byte> bytes;

        public BencodeString() {
            super();
        }

        public BencodeString(String str) throws UnsupportedEncodingException {
            super();
            this.bytes = new ArrayList<>();
            for (byte b : str.getBytes("utf-8")) {
                bytes.add(b);
            }
        }

        public BencodeString(List<Byte> bytes) {
            super();
            this.bytes = bytes;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append((char) b);
            }
            return sb.toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof BencodeString) {
                BencodeString o = (BencodeString) obj;
                if (o.toString().equals(this.toString())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + Objects.hashCode(this.bytes);
            return hash;
        }

        @Override
        public List<Byte> getBytes() {
            return bytes;
        }

        public void setBytes(List<Byte> bytes) {
            this.bytes = bytes;
        }
    }

    public class BencodeDictionary extends BencodeObject {

        private Map<BencodeString, BencodeObject> map;

        public BencodeDictionary() {
            super();
            this.map = new LinkedHashMap<>();
        }

        public Map<BencodeString, BencodeObject> getMap() {
            return map;
        }

        public void setMap(Map<BencodeString, BencodeObject> map) {
            this.map = map;
        }

        @Override
        public List<Byte> getBytes() {
            List<Byte> bytes = new ArrayList<>();
            bytes.add((byte) 'd');
            for (BencodeString k : map.keySet()) {
                bytes.addAll(k.getBytes());
                bytes.addAll(map.get(k).getBytes());
            }
            bytes.add((byte) 'e');
            return bytes;
        }
    }

    public class BencodeList extends BencodeObject {

        private List<BencodeObject> list;

        public BencodeList() {
            super();
            this.list = new ArrayList<>();
        }

        public List<BencodeObject> getList() {
            return list;
        }

        public void setList(List<BencodeObject> list) {
            this.list = list;
        }

        @Override
        public List<Byte> getBytes() {
            List<Byte> bytes = new ArrayList<>();
            bytes.add((byte) 'l');
            for (BencodeObject benObj : list) {
                bytes.addAll(benObj.getBytes());
            }
            bytes.add((byte) 'e');
            return bytes;
        }
    }

    public class Index {

        public int index;

        public Index() {
            this.index = 0;
        }
    }

    public BencodeString unbencodeString(byte[] input, Index index) {
        List<Byte> bytes = new ArrayList<>();
        int temp_index = index.index;
        int power_of_ten = 1;
        int length_of_string = 0;
        boolean first_digit = false;

        // Determine the length of the integer representing the String's length.
        while (input[temp_index] != (byte) ':') {
            if (first_digit) {
                power_of_ten *= 10;
            }
            first_digit = true;
            temp_index++;
        }

        // Determine the length of the string.
        while (input[index.index] != (byte) ':') {
            length_of_string += ((input[index.index] - 48) * power_of_ten);
            power_of_ten /= 10;
            bytes.add(input[index.index]);
            index.index++;

        }

        // Skip the ':'
        bytes.add(input[index.index]);
        index.index++;

        // Extract the string.
        while ((length_of_string > 0) && (index.index <= input.length)) {
            bytes.add(input[index.index]);

            length_of_string--;
            index.index++;
        }

        return new BencodeString(bytes);
    }

    public BencodeInteger unbencodeInteger(byte[] input, Index index) {
        List<Byte> bytes = new ArrayList<>();
        while (true) {
            byte b = input[index.index];
            bytes.add(b);
            index.index++;
            if (b == (byte) 'e') {
                break;
            }
        }
        return new BencodeInteger(bytes);
    }

    public BencodeDictionary unbencodeDictionary(byte[] input, Index index) {
        BencodeDictionary benDict = new BencodeDictionary();
        Map<BencodeString, BencodeObject> map = benDict.getMap();
        BencodeString key;
        BencodeObject value;

        // Skip the 'd'
        index.index++;

        int next_data_type = getEncodedType(input, index.index);

        // As long as there isn't an error or the end of our dictionary, keep
        // parsing the entries.
        while ((next_data_type != NULL_TYPE)
                && (next_data_type != STRUCTURE_END)
                && (index.index < input.length)) {
            // The key is ALWAYS a string.
            if (next_data_type != STRING) {
                System.err
                        .println("Error: The bencoded object beginning at index.index "
                                + index.index
                                + " is not a String, but must be according to the BitTorrent definition.");
            }

            key = unbencodeString(input, index);

            // Now get the input type of the value
            next_data_type = getEncodedType(input, index.index);

            switch (next_data_type) {
                case INTEGER:
                    value = unbencodeInteger(input, index);
                    break;
                case STRING:
                    value = unbencodeString(input, index);
                    break;
                case LIST:
                    value = unbencodeList(input, index);
                    break;
                case DICTIONARY:
                    value = unbencodeDictionary(input, index);
                    break;
                default:
                    System.err.println("Error: The value of the key \"" + key
                            + "\" is not a valid bencoded data type.");
                    return null;
            }

            map.put(key, value);
            // System.out.println("[" + key + "/" + value.toString() + "]");

            next_data_type = getEncodedType(input, index.index);
        }

        // Skip the 'e'
        index.index++;

        return benDict;
    }

    public BencodeList unbencodeList(byte[] input, Index index) {
        BencodeList benList = new BencodeList();
        List<BencodeObject> benObjs = benList.getList();

        // Skip the 'l'
        index.index++;

        int next_data_type = getEncodedType(input, index.index);

        while ((next_data_type != STRUCTURE_END)
                && (next_data_type != NULL_TYPE)
                && (index.index < input.length)) {
            switch (next_data_type) {
                case INTEGER:
                    benObjs.add(unbencodeInteger(input, index));
                    break;
                case STRING:
                    benObjs.add(unbencodeString(input, index));
                    break;
                case LIST:
                    benObjs.add(unbencodeList(input, index));
                    break;
                case DICTIONARY:
                    benObjs.add(unbencodeDictionary(input, index));
                    break;
                default:
                    System.err.println("Error: The object at position "
                            + index.index
                            + " is not a valid bencoded data type.");
                    return null;
            }
            next_data_type = getEncodedType(input, index.index);
        }

        // Skip the 'e'
        index.index++;

        return benList;
    }

    public int getEncodedType(byte[] input, int index) {
        // The value to be returned
        int return_value = NULL_TYPE;

        // Set return_value according to the byte at input[index]
        switch ((char) input[index]) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return_value = STRING;
                break;
            case 'i':
                return_value = INTEGER;
                break;
            case 'l':
                return_value = LIST;
                break;
            case 'd':
                return_value = DICTIONARY;
                break;
            case 'e':
                return_value = STRUCTURE_END;
                break;
            default:
                System.err
                        .println("Error: The byte at position "
                                + index
                                + " in the .torrent file is not the beginning of a bencoded data type.");
                break;
        }

        return return_value;
    }
}
