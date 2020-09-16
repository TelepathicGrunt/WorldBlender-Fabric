package net.telepathicgrunt.worldblender.dimension;

import java.util.Locale;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.telepathicgrunt.worldblender.WorldBlender;

public class RegUtil {
    @Nonnull
    public static <T> T injected() {
        return null;
    }

    public static <T extends IForgeRegistryEntry<T>> Generic<T> generic(IForgeRegistry<T> registry) {
        return new Generic<>(registry);
    }

    public static Blocks blocks(IForgeRegistry<Block> registry) {
        return new Blocks(registry);
    }

    public static Items items(IForgeRegistry<Item> registry) {
        return new Items(registry);
    }

    @SuppressWarnings("deprecation")
	public static void registerStructure(String key, StructureFeature<?> structure) {
        Registry.register(Registry.STRUCTURE_FEATURE, key.toLowerCase(Locale.ROOT), structure);
        Feature.STRUCTURES.put(key.toLowerCase(Locale.ROOT), structure);
    }

    public static class Items {
        private final IForgeRegistry<Item> registry;
        private Supplier<Item.Settings> propertiesSupplier = Item.Settings::new;

        private Items(IForgeRegistry<Item> registry) {
            this.registry = registry;
        }

        public Items withProperties(Supplier<Item.Settings> propertiesSupplier) {
            this.propertiesSupplier = propertiesSupplier;
            return this;
        }

        public Items add(String name, Item item) {
            Identifier registryName = GameData.checkPrefix(name, false);
            item.setRegistryName(registryName);

            this.registry.register(item);

            return this;
        }

        public Items add(String name, Function<Item.Settings, Item> function) {
            Item item = function.apply(this.propertiesSupplier.get());
            return this.add(name, item);
        }

        public Items add(Block block, BiFunction<Block, Item.Settings, Item> function) {
            Item item = function.apply(block, this.propertiesSupplier.get());
            item.setRegistryName(block.getRegistryName());

            this.registry.register(item);

            return this;
        }

        public Items addAll(BiFunction<Block, Item.Settings, Item> function, Block... blocks) {
            for (Block block : blocks) {
                add(block, function);
            }
            return this;
        }

        public Items add(String customName, Block block, BiFunction<Block, Item.Settings, Item> function) {
            this.registry.register(function.apply(block, this.propertiesSupplier.get()).setRegistryName(WorldBlender.MODID, customName));
            return this;
        }
    }

    public static class Blocks {
        private final IForgeRegistry<Block> registry;
        private Supplier<Block.Settings> propertiesSupplier;

        private Blocks(IForgeRegistry<Block> registry) {
            this.registry = registry;
        }

        public Blocks withProperties(Supplier<Block.Settings> propertiesSupplier) {
            this.propertiesSupplier = propertiesSupplier;
            return this;
        }

        public Blocks add(String name, Block block) {
            Identifier registryName = GameData.checkPrefix(name, false);
            block.setRegistryName(registryName);

            this.registry.register(block);

            return this;
        }

        public Blocks add(String name, Function<Block.Settings, Block> function) {
            Preconditions.checkNotNull(this.propertiesSupplier, "properties supplier not set");
            Block block = function.apply(this.propertiesSupplier.get());
            return this.add(name, block);
        }
    }

    public static class Generic<T extends IForgeRegistryEntry<T>> {
        private final IForgeRegistry<T> registry;

        private Generic(IForgeRegistry<T> registry) {
            this.registry = registry;
        }

        public Generic<T> add(String name, T entry) {
            Identifier registryName = GameData.checkPrefix(name, false);
            entry.setRegistryName(registryName);

            this.registry.register(entry);

            return this;
        }
    }
}