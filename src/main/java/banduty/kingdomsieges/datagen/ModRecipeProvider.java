package banduty.kingdomsieges.datagen;

import banduty.kingdomsieges.items.KSItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        offerMirroredRecipe(exporter, KSItems.RAMROD.get(), Map.of('S', Items.STICK, 'L', ItemTags.LOGS),
                "L  ", " S ", "  S");
    }

    private void offerMirroredRecipe(Consumer<RecipeJsonProvider> exporter, Item result,
                                     Map<Character, Object> inputs, String... pattern) {
        ShapedRecipeJsonBuilder normal = ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, result, 1);
        for (String line : pattern) normal.pattern(line);
        applyInputs(normal, inputs);
        addCriteria(normal, inputs);
        normal.offerTo(exporter, new Identifier(getRecipeName(result)));

        ShapedRecipeJsonBuilder mirrored = ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, result, 1);
        for (String line : pattern) mirrored.pattern(new StringBuilder(line).reverse().toString());
        applyInputs(mirrored, inputs);
        addCriteria(mirrored, inputs);
        mirrored.offerTo(exporter, new Identifier(getRecipeName(result) + "_mirrored"));
    }

    private void applyInputs(ShapedRecipeJsonBuilder builder, Map<Character, Object> inputs) {
        inputs.forEach((key, value) -> {
            if (value instanceof ItemConvertible item) {
                builder.input(key, item);
            } else if (value instanceof TagKey<?> tag) {
                @SuppressWarnings("unchecked")
                TagKey<Item> itemTag = (TagKey<Item>) tag;
                builder.input(key, itemTag);
            } else {
                throw new IllegalArgumentException("Unsupported input type: " + value);
            }
        });
    }

    private void addCriteria(ShapedRecipeJsonBuilder builder, Map<Character, Object> inputs) {
        inputs.forEach((key, value) -> {
            if (value instanceof Item item) {
                builder.criterion(hasItem(item), conditionsFromItem(item));
            } else if (value instanceof TagKey<?> tag) {
                @SuppressWarnings("unchecked")
                TagKey<Item> itemTag = (TagKey<Item>) tag;
                builder.criterion("has_" + tag.id().getPath(), conditionsFromTag(itemTag));
            }
        });
    }
}
