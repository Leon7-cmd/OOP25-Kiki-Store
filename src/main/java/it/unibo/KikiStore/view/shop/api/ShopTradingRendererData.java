package it.unibo.KikiStore.view.shop.api;

public interface ShopTradingRendererData {
    /**
     * @return true if the current tab is the buying tab, false otherwise
     */
    boolean isBuyingTab();

    /**
     * @return the index of the currently selected item in the list
     */
    int getSelectedIndex();
    
}
