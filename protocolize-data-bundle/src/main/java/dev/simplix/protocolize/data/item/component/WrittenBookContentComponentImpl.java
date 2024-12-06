package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.Book;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.item.component.WrittenBookContentComponent;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class WrittenBookContentComponentImpl implements WrittenBookContentComponent {

    private Book book;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        book.setTitle(ProtocolUtil.readString(byteBuf));
        if(byteBuf.readBoolean()){
            book.setFilteredTitle(ProtocolUtil.readString(byteBuf));
        }
        book.setAuthor(ProtocolUtil.readString(byteBuf));
        book.setGeneration(ProtocolUtil.readVarInt(byteBuf));
        int count = ProtocolUtil.readVarInt(byteBuf);
        for(int i = 0; i < count; i++){
            Book.Page page = new Book.Page();
            page.setContent(ChatElement.ofNbt(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion)));
            if(byteBuf.readBoolean()){
                page.setFilteredContent(ChatElement.ofNbt(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion)));
            }
            book.getPages().add(page);
        }
        book.setResolved(byteBuf.readBoolean());
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeString(byteBuf, book.getTitle());
        byteBuf.writeBoolean(book.getFilteredTitle() != null);
        if(book.getFilteredTitle() != null){
            ProtocolUtil.writeString(byteBuf, book.getFilteredTitle());
        }
        ProtocolUtil.writeString(byteBuf, book.getAuthor());
        ProtocolUtil.writeVarInt(byteBuf, book.getGeneration());
        ProtocolUtil.writeVarInt(byteBuf, book.getPages().size());
        for(Book.Page page : book.getPages()){
            NamedBinaryTagUtil.writeTag(byteBuf, page.getContent().asNbt(), protocolVersion);
            byteBuf.writeBoolean(page.getFilteredContent() != null);
            if(page.getFilteredContent() != null) {
                NamedBinaryTagUtil.writeTag(byteBuf, page.getFilteredContent().asNbt(), protocolVersion);
            }
        }
        byteBuf.writeBoolean(book.isResolved());
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<WrittenBookContentComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public WrittenBookContentComponent create(Book book) {
            return new WrittenBookContentComponentImpl(book);
        }

        @Override
        public String getName() {
            return "minecraft:written_book_content";
        }

        @Override
        public WrittenBookContentComponent createEmpty() {
            return new WrittenBookContentComponentImpl(new Book("", null, "", 1, new ArrayList<>(0), false));
        }

    }

}
