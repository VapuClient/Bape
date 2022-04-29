package mc.bape.api;

import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

import java.util.Iterator;
import java.util.List;

public class Disconnected {
    public static IChatComponent Dogged = new IChatComponent() {
        @Override
        public IChatComponent setChatStyle(ChatStyle chatStyle) {
            return null;
        }

        @Override
        public ChatStyle getChatStyle() {
            return null;
        }

        @Override
        public IChatComponent appendText(String s) {
            return null;
        }

        @Override
        public IChatComponent appendSibling(IChatComponent iChatComponent) {
            return null;
        }

        @Override
        public String getUnformattedTextForChat() {
            return null;
        }

        @Override
        public String getUnformattedText() {
            return null;
        }

        @Override
        public String getFormattedText() {
            return null;
        }

        @Override
        public List<IChatComponent> getSiblings() {
            return null;
        }

        @Override
        public IChatComponent createCopy() {
            return null;
        }

        @Override
        public Iterator<IChatComponent> iterator() {
            return null;
        }
    };
}
