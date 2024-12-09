package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.ToolRule;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.item.component.ToolComponent;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.Block;
import dev.simplix.protocolize.data.util.StructuredComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ToolComponentImpl implements ToolComponent {

    private List<ToolRule> rules;
    private float miningSpeed;
    private int damagePerBlock;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int count = ProtocolUtil.readVarInt(byteBuf);
        for(int i = 0; i < count; i++) {
            rules.add(readRule(byteBuf, protocolVersion));
        }
        miningSpeed = byteBuf.readFloat();
        damagePerBlock = ProtocolUtil.readVarInt(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, rules.size());
        for(ToolRule rule : rules) {
            writeRule(byteBuf, rule, protocolVersion);
        }
        byteBuf.writeFloat(miningSpeed);
        ProtocolUtil.writeVarInt(byteBuf, damagePerBlock);
    }

    private ToolRule readRule(ByteBuf byteBuf, int protocolVersion){
        ToolRule rule = new ToolRule();
        rule.setBlockSet(StructuredComponentUtil.readHolderSet(byteBuf, Block.class, protocolVersion));
        if(byteBuf.readBoolean()){
            rule.setSpeed(byteBuf.readFloat());
        }
        if(byteBuf.readBoolean()){
            rule.setCorrectToolForDrops(byteBuf.readBoolean());
        }
        return rule;
    }

    private void writeRule(ByteBuf byteBuf, ToolRule rule, int protocolVersion){
        StructuredComponentUtil.writeHolderSet(byteBuf, rule.getBlockSet(), Block.class, protocolVersion);
        boolean hasSpeed = rule.getSpeed() != null;
        byteBuf.writeBoolean(hasSpeed);
        if(hasSpeed){
            byteBuf.writeFloat(rule.getSpeed());
        }
        boolean hasCorrectToolForDrops = rule.getCorrectToolForDrops() != null;
        byteBuf.writeBoolean(hasCorrectToolForDrops);
        if(hasCorrectToolForDrops){
            byteBuf.writeBoolean(rule.getCorrectToolForDrops());
        }
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public void addRule(ToolRule rule) {
        rules.add(rule);
    }

    @Override
    public void removeRule(ToolRule rule) {
        rules.remove(rule);
    }

    @Override
    public void removeAllRules() {
        rules.clear();
    }

    public static class Type implements StructuredComponentType<ToolComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public ToolComponent create(List<ToolRule> rules, float miningSpeed, int damagePerBlock) {
            return new ToolComponentImpl(rules, miningSpeed, damagePerBlock);
        }

        @Override
        public String getName() {
            return "minecraft:tool";
        }

        @Override
        public ToolComponent createEmpty() {
            return new ToolComponentImpl(new ArrayList<>(0), 0, 0);
        }

    }

}
