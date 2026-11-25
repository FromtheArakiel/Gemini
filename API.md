# Gemini API Guide

Gemini is a Fabric 1.21.1 library organized by functional domain. Public API
types live at each domain root. Types under `internal` are implementation
details and are not intended for downstream use. Client-only code lives under
`client` packages and in client Mixin/entrypoint metadata.

## Modules

The repository is a Gradle multi-project build. `GeminiBase` is the core module
and produces the final `gemini` distribution JAR. The remaining modules are
independent nested Fabric mods with their own mod IDs, metadata and tests.

| Project | Mod ID | Java package |
| --- | --- | --- |
| `GeminiBase` | `gemini` | `dev.arakiel.gemini` |
| `GeminiRegistrationModule` | `gemini_registration` | `dev.arakiel.gemini.registration` |
| `GeminiEventModule` | `gemini_event` | `dev.arakiel.gemini.event` |
| `GeminiNetworkModule` | `gemini_network` | `dev.arakiel.gemini.network` |
| `GeminiComponentModule` | `gemini_component` | `dev.arakiel.gemini.component` |
| `GeminiCapabilityModule` | `gemini_capability` | `dev.arakiel.gemini.capability` |
| `GeminiConfigModule` | `gemini_config` | `dev.arakiel.gemini.config` |
| `GeminiLocateModule` | `gemini_locate` | `dev.arakiel.gemini.locate` |
| `GeminiGameplayModule` | `gemini_gameplay` | `dev.arakiel.gemini.attribute`, `.combat`, `.explosion`, `.tweak` |
| `GeminiIntegrationModule` | `gemini_integration` | `dev.arakiel.gemini.integration` |
| `GeminiClientModule` | `gemini_client` | `dev.arakiel.gemini.client` |
| `GeminiTabModule` | `gemini_tab` | `dev.arakiel.gemini.tab` |
| `GeminiCreativeModule` | `gemini_creative` | `dev.arakiel.gemini.creative` |

Module dependencies:

```text
GeminiBase
<- GeminiRegistrationModule
<- GeminiEventModule
<- GeminiNetworkModule
<- GeminiComponentModule
<- GeminiCapabilityModule -> GeminiComponentModule
<- GeminiConfigModule
<- GeminiLocateModule -> GeminiConfigModule, GeminiIntegrationModule
<- GeminiGameplayModule -> GeminiEventModule
<- GeminiIntegrationModule
<- GeminiClientModule -> GeminiEventModule
<- GeminiTabModule
<- GeminiCreativeModule
```

| Domain | Main entry point | Purpose |
| --- | --- | --- |
| `registration` | `GeminiRegistration` | Field-scanning registry containers |
| `event` | `GeminiEvents` | Shared event bus and Fabric bridges |
| `capability` | `GeminiCapabilities` | Cross-mod capability discovery, storage views, and explicit Fabric Transfer export |
| `component` | `GeminiComponents` | Entity/block/chunk/level/world/scoreboard and ItemStack components |
| `network` | `GeminiNetwork` | Typed channels, clientbound/serverbound messages, bundles |
| `config` | `GeminiConfig` | TOML configs, annotations, reload listeners, generated GUI |
| `attribute` | `GeminiAttributes`, `GeminiDamageTypes` | Shared attributes and damage types |
| `locate` | `GeminiLocate` | Async structure/biome lookup |
| `integration` | `GeminiIntegrations` | Lazy optional-mod integration loading |
| `combat` | `GeminiCombat` | Attack-triggered mob effects |
| `creative` | `GeminiCreativeTabs` | Creative tab registration |
| `explosion` | `GeminiExplosion` | Structured explosion configuration |
| `tab` | `GeminiTabs`, `TabRegistry` | Stateful inventory-screen tab registration and layout |
| `tweak` | `GeminiTweaks`, `FastUseItem` | Small gameplay extensions |
| `text` | `GeminiText` | Client text wrapping and drawing |

## Registration

Create a container interface and call `GeminiRegistration`. Public static
fields matching the container type are registered automatically.

```java
@RegistryNamespace("example_mod")
public interface ModItems extends ItemRegistryContainer {
    Item RUBY = new Item(new Item.Properties());
}

GeminiRegistration.register(ModItems.class, "example_mod");
```

Available specialized containers:

- `ItemRegistryContainer`
- `BlockRegistryContainer`
- `BlockEntityRegistryContainer`
- `EntityRegistryContainer`
- `MobEffectRegistryContainer`
- `SoundEventRegistryContainer`

`AutoRegistryContainer` can be implemented for any Minecraft registry.
`postProcessField` lets a container customize a value after registration.

Convenience methods such as `GeminiRegistration.items(...)` use the `gemini`
namespace. A dependent mod should normally pass its own namespace or use
`@RegistryNamespace`.

## Events

Listeners can be registered with lambdas or using `@SubscribeEvent`.

```java
Subscription subscription = GeminiEvents.listen(
        LivingHurtEvent.class,
        EventPriority.NORMAL,
        event -> {
            event.setAmount(event.getAmount() * 0.5F);
        }
);

subscription.close();
```

Event classes are grouped under `event/block`, `event/command`,
`event/client`, `event/entity`, `event/explosion`, `event/item`,
`event/level`, `event/resource`, `event/server`, `event/spawn`, and related
packages.

```java
@SubscribeEvent
public void onHurt(LivingHurtEvent event) {
}
```

`GeminiEvents.register(instance)` scans annotated listener methods.
`GeminiEvents.post(event)` dispatches an event immediately.

P0 runtime sets include:

- command registration, parse and execute observation
- datapack reload/sync, tag reload and loot-table replacement
- player clone, respawn, dimension change, container and tick lifecycle
- player/entity block placement, block drops, farmland, crop and explosion
- mob spawn finalization, spawn position checks and living drops/experience
- client tick, player network, screen/container render, tooltip and registration
  callbacks

Use `GeminiServerHooks` for reload listeners and built-in packs, and
`GeminiGameplayHooks.registerSpawnPlacements()` when applying
`RegisterSpawnPlacementsEvent`.

## Capability

Capability discovery is separate from Components and from Fabric Transfer.
Components may back a capability provider, but a provider can also return a
dynamic view with no persistent component.

```java
CapabilityKey<MyView> MY_VIEW =
        CapabilityKey.create(ResourceLocation.parse("example_mod:my_view"));

GeminiCapabilities.registry().registerBlockEntity(
        MY_VIEW,
        MOD_BLOCK_ENTITY,
        context -> Optional.of(new MyView(context.direction())),
        CapabilityPriority.NORMAL
);

Optional<MyView> view = GeminiCapabilities.registry().find(
        MY_VIEW,
        new BlockCapabilityContext(level, pos, state, blockEntity, direction)
);
```

Registration is ordered by source specificity, then provider priority
descending, then registration order. The first query seals the registry.
Providers returning empty are skipped; provider exceptions are logged and
isolated from later providers.

Standard keys:

- `GeminiCapabilities.ITEM_STORAGE`
- `GeminiCapabilities.FLUID_STORAGE`
- `GeminiCapabilities.ENERGY`

`ItemStorageView` and `FluidStorageView` use simulation-safe operations.
ItemStack and `GeminiFluidStack` data components remain attached to stored
resources. `GeminiFluidStack` uses Gemini-owned `long` amounts.

Component-backed providers:

```java
CapabilityProvider<MyView, BlockCapabilityContext> provider =
        CapabilityComponents.blockEntity(MY_COMPONENT, MyView::new);
```

Capability registration never exposes a Fabric Transfer lookup automatically.
Export explicitly:

```java
GeminiFabricTransfer.exportItemStorage(MOD_BLOCK_ENTITY);
GeminiFabricTransfer.exportFluidStorage(MOD_BLOCK);
GeminiFabricTransfer.exportContainedItemStorage(MOD_ITEM);
```

The Fabric adapter preserves item/fluid variants and applies transaction
rollback through `TransactionContext`. Gemini energy remains its own protocol;
it is never reported as FE/RF.

## Components

Gemini Components attaches serializable data to entities, block entities,
chunks, levels, worlds, scoreboard objects, and ItemStacks.

A component implements `Component` and defines stable NBT serialization.

```java
public final class Stamina implements Component {
    public static final ComponentKey<Stamina> KEY =
            GeminiComponents.key("stamina", Stamina.class);

    private int value;

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider registries) {
        value = tag.getInt("value");
    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("value", value);
    }
}
```

Access:

```java
Stamina stamina = GeminiComponents.get(Stamina.KEY, entity);
Stamina optional = GeminiComponents.getNullable(Stamina.KEY, entity);
GeminiComponents.sync(Stamina.KEY, entity);
```

`ConditionalToken` and `TokenKey` provide short-lived player state. See
`GeminiComponents.token`, `getToken`, and `getOrCreateToken`.

## Tabs

`GeminiTabs.registry()` returns the client tab state machine. Registration is
accepted while the registry is collecting definitions and is sealed on the
first layout request.

```java
TabDefinition definition = TabDefinition.builder(ResourceLocation.parse("example_mod:guide"))
        .owner("example_mod")
        .title(Component.translatable("screen.example_mod.guide"))
        .icon(TabIcon.item(Items.BOOK))
        .action(context -> Minecraft.getInstance().setScreen(new GuideScreen()))
        .selectedWhen(context -> context.screen() instanceof GuideScreen)
        .build();

TabRegistration result = GeminiTabs.registry().register(definition);
```

Anchored tabs request a position relative to other external tabs. Equal
anchors are sorted by resource identifier, then moved forward when a slot is
occupied. Unanchored tabs are sorted by queue lane and identifier, and fill
the remaining gaps before appending at the end.

```java
TabOrder.anchored(20);
TabOrder.queued();
TabOrder.queued(100);
```

The built-in Inventory tab is not a normal registration. It is hidden while no
visible external tabs exist and is always the first rendered tab once one
exists, including when another tab anchors at zero.

Container screens can implement `TabScreen` to receive the shared bar. The
screen should expose the external tab's selected state through
`TabDefinition.selectedWhen`. At most one tab is selected: the first visible
external tab whose predicate matches; otherwise Inventory is selected on
`InventoryScreen`.

L2-style selected backgrounds use `sel_*` and unselected backgrounds use
`des_*`. Hovering adds a short 120 ms brightness transition without changing
the selected identity.

Component factories are available through `ComponentFactoryRegistries`.
Automatic client synchronization uses `ComponentSync`, including
`AutoSyncedComponent`, `PacketWriter`, and `PlayerPredicate`.

### ItemStack Components

`ItemStack` is a component provider. Register factories through
`ComponentFactoryRegistries.ItemStack`, normally from a
`ComponentInitializers.ItemStack` entrypoint.

```java
public final class Attunement implements Component {
    public static final ComponentKey<Attunement> KEY =
            GeminiComponents.key("attunement", Attunement.class);

    private int level;

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider registries) {
        level = tag.getInt("level");
    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider registries) {
        if (level != 0) {
            tag.putInt("level", level);
        }
    }
}

public final class ItemComponentRegistration
        implements ComponentInitializers.ItemStack {
    @Override
    public void registerItemStackComponentFactories(
            ComponentFactoryRegistries.ItemStack registry
    ) {
        registry.beginRegistration(Attunement.KEY)
                .end(stack -> new Attunement());
    }
}
```

Keep the initializer class in the `gemini:components-itemstack` entrypoint.
Component ids themselves must still be declared through the normal Gemini
component metadata or static initializer.

Access uses the common component API:

```java
Attunement attunement = GeminiComponents.get(Attunement.KEY, itemStack);
Attunement optional = GeminiComponents.getNullable(Attunement.KEY, itemStack);
```

ItemStack components are stored in the persistent and network-synchronized
`gemini:item_components` data component. They therefore travel with normal
ItemStack saving and packets; a separate ItemStack sync packet is not needed.
The `writeToNbt` method should omit default values so an unused component does
not create a data-component entry.

- Persistent component NBT affects `ItemStack.matches` and
  `isSameItemSameComponents`, so stacks with different component values do not
  merge.
- A `TransientComponent` is kept in the live runtime container but is not
  persisted, synchronized, or considered by stack matching.
- `copy`, `copyWithCount`, and `split` create independent runtime containers.
  Implement `CopyableComponent` when mutable transient state needs custom copy
  behavior; otherwise the copied transient instance starts at its factory
  default.
- Empty ItemStacks never receive component data.
- Unknown serialized component keys are preserved when Gemini commits known
  component updates, which avoids discarding data owned by optional mods.
- `ItemStack.parse`, `save`, and the vanilla network codecs provide registry
  access to component NBT. Direct use of the internal data codec without a
  provider uses `RegistryAccess.EMPTY`.

## Networking

Channels use one codec and one direction registration per message class.

```java
public record StateUpdate(int value) {
    public static final StreamCodec<RegistryFriendlyByteBuf, StateUpdate> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    StateUpdate::value,
                    StateUpdate::new
            );
}

public static final NetworkChannel CHANNEL =
        GeminiNetwork.channel("example/state").optional();

CHANNEL.registerClientbound(
        StateUpdate.class,
        StateUpdate.CODEC,
        ClientHandlers::handleStateUpdate
);
```

Sending:

```java
CHANNEL.sendTo(player, new StateUpdate(10));
CHANNEL.sendToAll(server, new StateUpdate(10));
CHANNEL.sendToNear(level, center, 32.0D, new StateUpdate(10));
CHANNEL.sendToTracking(entity, new StateUpdate(10));
```

Serverbound registration accepts a `BiConsumer<T, ServerPlayer>`. Required
channels participate in handshake checks; optional channels are advertised but
not required on the client.

Bundles are sent atomically through methods such as `sendBundle`,
`sendBundleToAll`, `sendBundleToNear`, and `sendBundleToTracking`.

`RecordCodecs` creates a `StreamCodec` for simple record components.
`NetworkCodecs` exposes common bounded and primitive codecs.

## Config

Config classes use TOML and annotation-driven GUI generation.

```java
@Config(value = "gameplay", nested = true)
public final class GameplayConfig {
    @ConfigEntry(value = "combat.critMultiplier", comment = "Critical hit multiplier.")
    @Range(min = 1.0D, max = 10.0D)
    public double critMultiplier = 2.0D;
}

ConfigHandle<GameplayConfig> handle =
        GeminiConfig.register(GameplayConfig.class);

double value = handle.value().critMultiplier;
handle.addListener(config -> reloadCombatValues(config));
```

Supported metadata includes `@Range`, `@Hidden`, and `@RestartRequired`.
Configs are stored under `config/<namespace>/` when `nested` is enabled.

`ConfigHandle` also supports option inspection, staged updates, immediate
updates, save, reload, reset, and listener removal.

## Attributes And Damage Types

Gemini registers the shared L2-compatible attribute set under the `gemini`
namespace:

- `crit_rate`, `crit_damage`
- `projectile_damage`, `explosion_damage`, `fire_damage`
- `freezing_damage`, `lightning_damage`, `magic_damage`
- `regeneration_rate`, `damage_absorption`, `damage_reduction`

Use `GeminiAttributeModifiers` for transient modifiers:

```java
GeminiAttributeModifiers.set(
        entity,
        GeminiAttributes.CRIT_DAMAGE,
        ResourceLocation.fromNamespaceAndPath("example_mod", "weapon_bonus"),
        0.25D,
        AttributeModifier.Operation.ADD_VALUE
);
```

Damage types are exposed by `GeminiDamageTypes`:

- `bleed`, `soul_flame`, `freeze`, `life_sync`, `void_eye`, `emerald`
- Fourteen `player_attack` / `mob_attack` bypass combinations

Use the resource keys directly:

```java
ResourceKey<DamageType> type = GeminiDamageTypes.BLEED;
```

The corresponding JSON files are in `data/gemini/damage_type`.

## Async Locate

`GeminiLocate` wraps the server locate pipeline with bounded concurrency,
queue limits, task coalescing, timeouts, and main-thread callbacks.

```java
GeminiLocate.findBiome(
        level,
        holder -> holder.is(Biomes.PLAINS),
        origin,
        6400,
        32,
        64
).withTimeout(30, TimeUnit.SECONDS)
        .handleOnServerThread((result, error) -> {
            if (error != null) {
                report(error);
            } else if (result != null) {
                usePosition(result.getFirst());
            }
        });
```

Structure lookup accepts either a `TagKey<Structure>` or `HolderSet<Structure>`.
`LocateTask` supports `handle`, `handleOnServerThread`, `map`, timeout, and
cancellation.

`LocateConfig` controls concurrency, queue size, feature toggles, biome search
radius, thread type, and timeouts.

## Optional Mod Integrations

Integration classes are loaded only when the target mod and version range
match.

```java
@GeminiIntegration(
        value = "jei",
        version = "[19.0,)",
        type = GeminiIntegrationType.CLIENT
)
public final class JeiIntegration {
    public void applyClient() {
    }
}
```

Supported lifecycle methods:

- `apply` for common/server work
- `applyClient`
- `applyServer`
- `applyData`

The scanner reads annotations without loading target classes. Query installed
or active integrations with `GeminiIntegrations.isModLoaded`,
`hasActiveIntegration`, and `activeMods`.

## Gameplay APIs

Attack-triggered effects:

```java
GeminiCombat.attackEffect(
        weapon,
        MobEffects.POISON,
        100,
        0
).register();
```

Creative tabs:

```java
ResourceKey<CreativeModeTab> tab = GeminiCreativeTabs.register(
        "example_tab",
        Component.literal("Example"),
        () -> new ItemStack(ModItems.RUBY),
        output -> output.accept(ModItems.RUBY)
);
```

Explosions:

```java
GeminiExplosion.builder(level, x, y, z, 4.0F)
        .interaction(Level.ExplosionInteraction.BLOCK)
        .source(source)
        .block(blockBehavior)
        .entities(entityBehavior)
        .presentation(GeminiExplosion.Presentation.standard())
        .explode();
```

Small tweaks:

- `GeminiTweaks.makeLightningSafe`
- `GeminiTweaks.setArrowDespawnFactor`
- Implement `FastUseItem` on an item to skip use slowdown
- `GeminiText` provides wrapping and drawing helpers for client screens

## Environment Rules

- `main` and `client` source sets are merged; client-only classes are separated
  by package and Fabric entrypoints.
- Dedicated-server code must not reference `client` classes directly.
- Client-bound handlers belong in `network.client` or another client package.
- Registration must occur during initialization, before registries and network
  channels are frozen.
- `internal` packages may change without compatibility guarantees.

## Build

```text
gradlew.bat test
gradlew.bat build
gradlew.bat testModule -Pmodule=GeminiCapabilityModule
gradlew.bat buildModule -Pmodule=GeminiCapabilityModule
```

The root `test` task runs tests for every module. The root `build` task builds
all modules and produces the final core distribution at:

```text
GeminiBase/build/libs/gemini-<version>.jar
```

That JAR contains the independent feature mods under `META-INF/jars/`.

`testModule` and `buildModule` use Gradle project dependencies, so compiling or
testing `GeminiCapabilityModule`, for example, automatically builds
`GeminiComponentModule` and `GeminiBase` first.
