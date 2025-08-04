# 3ng1n3 - Cyberpunk Chat Application 🚀

A cutting-edge cyberpunk-themed chat application built on Solana blockchain with cNFT minting capabilities, cryptocurrency payments, and advanced encryption features.

## 🌟 Features

### Core Functionality
- **Cyberpunk Chat Interface**: Immersive dark-themed chat experience
- **Wallet Integration**: Seamless Solana wallet connectivity
- **Message Encryption/Decryption**: Secure messaging for wallet users
- **Binary Translator**: Convert text with car engine sound effects

### cNFT Minting
- **Frame Selection**: Choose from 4 unique cyberpunk frames
  - F1: Holographic (Common)
  - F2: Neon Glow (Uncommon)
  - F3: Cyber Matrix (Rare)
  - F4: Quantum Flux (Legendary)

### Cryptocurrency Payments
- **Multi-Token Support**: 
  - SOL: 0.01 SOL (~$2.50)
  - BONK: 50,000 BONK (~$2.50)
  - USDC: 2.50 USDC (~$2.50)
- **Real-time Balance Updates**: Automatic wallet balance tracking
- **Transaction History**: Complete payment and minting records

## 🏗️ Project Structure

```
3ng1n3_app/
├── app/                          # Android application
│   ├── src/main/java/com/engine/app/
│   │   ├── activities/           # Activity classes
│   │   ├── data/                # Data repositories
│   │   ├── models/              # Data models
│   │   └── viewmodels/          # ViewModels
│   └── build.gradle             # Android build configuration
├── app_preview.html             # Web preview application
├── txtx/                        # txtx blockchain framework
│   ├── examples/3ng1n3-test/    # Custom txtx runbook
│   ├── addons/                  # Blockchain integrations
│   └── crates/                  # Core txtx components
├── txtx-test-simulation.ps1     # Deployment simulation script
└── README.md                    # This file
```

## 🚀 Getting Started

### Prerequisites
- Android Studio (for mobile app)
- Rust (for txtx framework)
- Node.js (for web components)
- Solana CLI
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd 3ng1n3_app
   ```

2. **Setup Android App**
   ```bash
   cd app
   ./gradlew build
   ```

3. **Setup txtx Framework**
   ```bash
   cd txtx
   cargo build --release
   ```

4. **Run Web Preview**
   ```bash
   # Open app_preview.html in browser or run local server
   python -m http.server 8000
   ```

### Deployment

#### Testnet Deployment
```bash
# Run the simulation script
powershell -ExecutionPolicy Bypass -File txtx-test-simulation.ps1

# Or use txtx directly
cd txtx/examples/3ng1n3-test
txtx runbook run main.tx
```

#### Mainnet Deployment
1. Update `txtx.yml` network configuration to "mainnet"
2. Update `main.tx` runbook target network
3. Run deployment with production wallet

## 🔧 Configuration

### Network Settings
- **Testnet**: `https://api.testnet.solana.com`
- **Mainnet**: `https://api.mainnet-beta.solana.com`

### Environment Variables
```bash
SOLANA_NETWORK=testnet
WALLET_PRIVATE_KEY=your_private_key
RPC_ENDPOINT=https://api.testnet.solana.com
```

## 🎮 Usage

### Web Application
1. Open `app_preview.html` in your browser
2. Connect your Solana wallet
3. Start chatting and minting cNFTs
4. Select payment method (SOL/BONK/USDC)
5. Choose frame style and mint

### Android Application
1. Install the APK on your device
2. Connect Solana wallet
3. Access all features through native interface

## 🔐 Security Features

- **Wallet Verification**: Only connected wallets can decrypt messages
- **Secure Transactions**: All blockchain interactions are cryptographically signed
- **Private Key Protection**: Keys never leave the user's device
- **Message Encryption**: End-to-end encryption for sensitive communications

## 🛠️ Development

### Adding New Features
1. Update the appropriate component (Android/Web/txtx)
2. Test with simulation script
3. Deploy to testnet for validation
4. Submit pull request

### Testing
```bash
# Run Android tests
cd app && ./gradlew test

# Run txtx tests
cd txtx && cargo test

# Run simulation
powershell txtx-test-simulation.ps1
```

## 📱 Supported Platforms

- **Web**: Modern browsers with Web3 wallet support
- **Android**: API level 24+ (Android 7.0+)
- **Blockchain**: Solana Testnet/Mainnet

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- **txtx Framework**: Blockchain automation and deployment
- **Solana**: High-performance blockchain platform
- **Metaplex**: cNFT standards and tools
- **Community**: All contributors and testers

## 📞 Support

For support and questions:
- Create an issue in this repository
- Join our Discord community
- Check the documentation in `/doc`

---

**Built with ❤️ for the cyberpunk future** 🌆