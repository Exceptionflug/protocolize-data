package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.item.component.ToolComponent;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.util.StructureComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
public class ToolComponentImpl implements ToolComponent {

    private List<Rule> rules;
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
        for(Rule rule : rules) {
            writeRule(byteBuf, rule, protocolVersion);
        }
        byteBuf.writeFloat(miningSpeed);
        ProtocolUtil.writeVarInt(byteBuf, damagePerBlock);
    }

    private Rule readRule(ByteBuf byteBuf, int protocolVersion){
        Rule rule = new Rule();
        rule.setBlockSet(StructureComponentUtil.readBlockSet(byteBuf, protocolVersion));
        if(byteBuf.readBoolean()){
            rule.setSpeed(byteBuf.readFloat());
        }
        if(byteBuf.readBoolean()){
            rule.setCorrectToolForDrops(byteBuf.readBoolean());
        }
        return rule;
    }

    private void writeRule(ByteBuf byteBuf, Rule rule, int protocolVersion){
        StructureComponentUtil.writeBlockSet(byteBuf, rule.getBlockSet(), protocolVersion);
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
    public void addRule(Rule rule) {
        rules.add(rule);
    }

    @Override
    public void removeRule(Rule rule) {
        rules.remove(rule);
    }

    @Override
    public void removeAllRules() {
        rules.clear();
    }

    public static class Type implements StructuredComponentType<ToolComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 22)
        );

        @Override
        public ToolComponent create(List<Rule> rules, float miningSpeed, int damagePerBlock) {
            return new ToolComponentImpl(rules, miningSpeed, damagePerBlock);
        }

        @Override
        public String getName() {
            return "minecraft:tool";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public ToolComponent createEmpty() {
            return new ToolComponentImpl(new ArrayList<>(0), 0, 0);
        }

    }

}
