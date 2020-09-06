# Edgestitch Permissions
A permissions mod for Minecraft dedicated servers built with the Fabric API that utilizes native
op levels. Permissions are decided and hardcoded by dependent mod implementations.

## Usage
Permissions are defined by Fabric mods in the `"custom"` field of `fabric.mod.json` file. 

For example:
```javascript
{
  // ...

  "custom": {
    "edgestitch-permissions": {
      "operator": {
        "description": "Allows the player to operate edgestitch commands.",
        "default": 4
      }
    }
  }
}
```
