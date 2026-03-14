package banduty.kingdomsieges.datagen;

import banduty.kingdomsieges.items.KSItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.Map;
import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        offerMirroredRecipe(exporter, KSItems.RAMROD, Map.of('S', Items.STICK, 'L', ItemTags.LOGS),
                "L  ", " S ", "  S");
    }

    private void offerMirroredRecipe(Consumer<FinishedRecipe> exporter, Item result,
                                     Map<Character, Object> inputs, String... pattern) {
        ShapedRecipeBuilder normal = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result, 1);
        for (String line : pattern) normal.pattern(line);
        applyInputs(normal, inputs);
        addCriteria(normal, inputs);
        normal.save(exporter, new ResourceLocation(getSimpleRecipeName(result)));

        ShapedRecipeBuilder mirrored = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result, 1);
        for (String line : pattern) mirrored.pattern(new StringBuilder(line).reverse().toString());
        applyInputs(mirrored, inputs);
        addCriteria(mirrored, inputs);
        mirrored.save(exporter, new ResourceLocation(getSimpleRecipeName(result) + "_mirrored"));
    }

    private void applyInputs(ShapedRecipeBuilder builder, Map<Character, Object> inputs) {
        inputs.forEach((key, value) -> {
            if (value instanceof ItemLike item) {
                builder.define(key, item);
            } else if (value instanceof TagKey<?> tag) {
                @SuppressWarnings("unchecked")
                TagKey<Item> itemTag = (TagKey<Item>) tag;
                builder.define(key, itemTag);
            } else {
                throw new IllegalArgumentException("Unsupported input type: " + value);
            }
        });
    }

    private void addCriteria(ShapedRecipeBuilder builder, Map<Character, Object> inputs) {
        inputs.forEach((key, value) -> {
            if (value instanceof Item item) {
                builder.unlockedBy(getHasName(item), has(item));
            } else if (value instanceof TagKey<?> tag) {
                @SuppressWarnings("unchecked")
                TagKey<Item> itemTag = (TagKey<Item>) tag;
                builder.unlockedBy("has_" + tag.location().getPath(), has(itemTag));
            }
        });
    }
}
