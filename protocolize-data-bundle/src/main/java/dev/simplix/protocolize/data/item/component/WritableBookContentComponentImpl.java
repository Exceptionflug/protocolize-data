package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.item.component.WritableBookContentComponent;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.util.ProtocolUtil;
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
public class WritableBookContentComponentImpl implements WritableBookContentComponent {

    private List<WriteablePage> pages;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int count = ProtocolUtil.readVarInt(byteBuf);
        for(int i = 0; i < count; i++){
            WriteablePage writeablePage = new WriteablePage();
            writeablePage.setContent(ProtocolUtil.readString(byteBuf));
            if(byteBuf.readBoolean()){
                writeablePage.setFilteredContent(ProtocolUtil.readString(byteBuf));
            }
            pages.add(writeablePage);
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, pages.size());
        for(WriteablePage page : pages){
            ProtocolUtil.writeString(byteBuf, page.getContent());
            byteBuf.writeBoolean(page.getFilteredContent() != null);
            if(page.getFilteredContent() != null) {
                ProtocolUtil.writeString(byteBuf, page.getFilteredContent());
            }
        }
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public void addPage(WriteablePage page) {
        pages.add(page);
    }

    @Override
    public void removePage(WriteablePage page) {
        pages.remove(page);
    }

    @Override
    public void removeAllPages() {
        pages.clear();
    }

    public static class Type implements StructuredComponentType<WritableBookContentComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 33)
        );

        @Override
        public WritableBookContentComponent create(List<WriteablePage> pages) {
            return new WritableBookContentComponentImpl(pages);
        }

        @Override
        public String getName() {
            return "minecraft:writable_book_content";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public WritableBookContentComponent createEmpty() {
            return new WritableBookContentComponentImpl(new ArrayList<>(0));
        }

    }

}
