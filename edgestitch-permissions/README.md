# Edgestitch Permissions
A permissions mod for Minecraft dedicated servers built with the Fabric API that utilizes native
op levels. Permissions are decided and hardcoded by any Fabric mod choosing to implement them.

## Usage
### Setting up the Fabric Mod JSON
Permissions are defined by Fabric mods in the `"custom"` field of `fabric.mod.json` file. 

For example, this is an excerpt from Edgestitch Permission's `fabric.mod.json`:
```none
  "custom": {
    "edgestitch-permissions": {
      "operator": {
        "description": "Allows the player to operate edgestitch-permissions commands.",
        "required_assign_op": 4
      }
    }
  }
```
Let's break this down:
- `"edgestitch-permissions": { }` defines the permissions for the associated mod - in this
 example the mod is Edgestitch Permissions itself.
   - Specifically, permissions are tied to the `"id"` field in the `fabric.mod.json` file. 
   For example, if you look at Edgestitch Permission's Fabric mod json you will see 
   `"id": "edgestitch-permissions"`.
- `"operator": { }"` defines a single permission named "operator". Inside, it has:
  - `"description"` - a brief description for the permission.
  - `"required_assign_op"` - **IMPORTANT**: the op level (0-4) required to **assign** this
  permission to a player. This does **NOT** define the op level required have or "use" the 
  permission.
    - In this example, only server players who are op level 4 can **assign** this permission to 
    other players. Once assigned, the target player has access to anything associated with this 
    permission.
    
### Core Edgestitch Permission API
The core components of the Edgestitch Permission API are:
- `Permissible` 
  - An interface that allows access to the persistent permissions of any supported Minecraft class.
  - Supported Minecraft classes:
    - `ServerPlayerEntity`
- `Permission`
  - Defines the content of a permission.

### Using Edgestitch Permissions API
#### Fully Qualified Names
A permission must be referred to by its **fully qualified name** string. The fully qualified name 
is a combination of the source mod ID and the permission name:

`"modid.permission_name"`

For example, in the section [Setting up the Fabric Mod JSON](#Setting-up-the-Fabric-Mod-JSON), the
example's mod ID was `"edgestitch-permissions"` and the permission name was `"operator"`. Thus, the
fully qualified name is:

`"edgestitch-permissions.operator"`

#### In Code
To access the permissions of a supported Minecraft class, cast the object to `Permissible`.
```java
// Java example.
public void example(ServerPlayerEntity player) {
  ((Permissible) player).hasPermission("edgestitch-permissions.operator");
}
```
```kotlin
// Kotlin example.
fun example(player: ServerPlayerEntity) {
  (player as Permissible).hasPermission("edgestitch-permissions.operator")
}
``` 

## Commands
Brigadier commands associated with this mod:
- `/edgestitch list <player name>`
  - Lists the active permissions a player has.
    - A permission is active if its source mod has been loaded by the Fabric mod loader.
  - Example usage: `/edgestitch list Notch`
- `/edgestitch add <player name> <fully qualified permission>`
  - Adds a permission to a player's data file.
    - The permission must be active to add.
  - Example usage: `/edgestitch add Notch edgestitch-permissions.operator`
- `/edgestitch remove <player name> <fully qualified permission>`
  - Removes a permission from a player's data file.
    - The permission does **NOT** have to be active. However, it must exist in the player's 
    data file.
  - Example usage: `/edgestitch remove Notch edgestitch-permissions.operator`

All of these commands require the `edgestitch-permissions.operator` permission to use. If you are
starting a server for the first time, you will need to perform:

`edgestitch add PlayerName edgestitch-permissions.operator`

in the server console, where `PlayerName` is replaced by the name of a Minecraft account that has 
connected to the server at least once (a re-connect may be necessary afterwards). Otherwise, the 
server console must be used to assign any Edgestitch permission.
