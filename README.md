# Solace PubSub+ Mission Control Plugin for IntelliJ IDEA

![Build](https://github.com/SolaceLabs/solace-mc-intellij-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)

<!-- Plugin description -->
Leverage the power of [Solace's Mission Control](https://solace.com/products/event-broker/cloud/mission-control/) right from IntelliJ.
<!-- Plugin description end -->

![Plugin Demo](doc/plugin_demo.gif)

## Features
* Create event broker services
* Delete event broker services
* View event broker services
* Publish and subscribe to an event broker service

## Usage
* Create a [Solace Cloud API token](https://docs.solace.com/Cloud/ght_api_tokens.htm?Highlight=api%20tokens#Create) with the following permissions (either the "My Services" or "Organizational Services" variants):
  * Get Services
  * (Optional) Create Services
  * (Optional) Delete Services
* Enter the API token in the plugin settings: `Settings/Preferences`>`Tools`>`Solace Mission Control` ![Tool Preferences](doc/tool_settings.png)
* (Optional) If you use SSO, specify the URL you use to access Solace Cloud