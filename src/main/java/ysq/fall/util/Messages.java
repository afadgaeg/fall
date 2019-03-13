package ysq.fall.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ysq.fall.util.Message.Severity;

public class Messages implements Serializable {

    private final Map<String, List<Message>> keyedMessages = new HashMap<>();
    private final List<Message> messages = new ArrayList<>();

    // ----------------------------

    public Messages() {
    }

    public void clear() {
        keyedMessages.clear();
        messages.clear();
    }

    // ------------------------------

    public void addToControl(String key, Severity severity, String summary, String detail) {
        Message message = new Message(severity, summary, detail);
        if (keyedMessages.containsKey(key)) {
            keyedMessages.get(key).add(message);
        } else {
            List<Message> list = new ArrayList<>();
            list.add(message);
            keyedMessages.put(key, list);
        }
    }

    public void add(Severity severity, String summary, String detail) {
        messages.add(new Message(severity, summary, detail));
    }

    public void clearAndAdd(Severity severity, String summary, String detail) {
        clear();
        add(severity, summary, detail);
    }

    // -----------------------------------

    public List<Message> getMessages() {
        return messages;
    }

    public Map<String, List<Message>> getKeyedMessages() {
        return keyedMessages;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(Message msg :messages){
            sb.append(msg.getSeverity().name()).append(":").append(msg.getSummary()).append("\n");
        }
        return sb.toString();
    }
    
    public String toKeyedMessagesString(){
        return JSONObject.parseObject(JSON.toJSONString(keyedMessages)).toJSONString();
    }

}
