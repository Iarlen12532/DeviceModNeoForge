# MrCrayfish's Device Mod - NeoForge 1.21.1

Recriação do mod **MrCrayfish's Device Mod** para **Minecraft 1.21.1** com **NeoForge**.

## Sobre o Mod

Este mod adiciona dispositivos eletrônicos funcionais ao Minecraft:
- **Laptop** - Computador portátil com interface gráfica
- **Printer** - Impressora com sistema de papel e tinta
- **Router** - Roteador para conectar dispositivos em rede
- **Flash Drive** - Pen drive para armazenar dados

## Requisitos

- Minecraft **1.21.1**
- NeoForge **21.1.0+**
- Java **21**

## Como Compilar

### No PC (Recomendado)
```bash
./gradlew build
```
O arquivo `.jar` será gerado em `build/libs/`.

### No Celular (GitHub Actions)
1. Faça fork deste repositório
2. Edite os arquivos pelo celular (GitHub web ou app)
3. O GitHub Actions compila automaticamente!
4. Baixe o `.jar` na aba **Actions** → **Artifacts**

## Estrutura do Projeto

```
src/main/java/com/mrcrayfish/devicemod/
├── DeviceMod.java              # Classe principal
├── block/                      # Blocos (Laptop, Printer, Router)
├── block/entity/               # Block Entities
├── client/gui/                 # Telas GUI
├── item/                       # Itens personalizados
├── menu/                       # Containers/Menus
├── registry/                   # Registros (Blocos, Itens, etc.)
└── app/                        # Sistema de aplicativos (futuro)
```

## Licença

MIT - Baseado no mod original de **MrCrayfish**.
