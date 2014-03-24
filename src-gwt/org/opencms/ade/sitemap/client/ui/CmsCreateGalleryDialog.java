/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.ade.sitemap.client.ui;

import org.opencms.ade.sitemap.client.control.CmsSitemapController;
import org.opencms.ade.sitemap.client.ui.css.I_CmsSitemapLayoutBundle;
import org.opencms.ade.sitemap.shared.CmsGalleryType;
import org.opencms.gwt.client.Messages;
import org.opencms.gwt.client.ui.CmsPopup;
import org.opencms.gwt.client.ui.CmsPushButton;
import org.opencms.gwt.client.ui.I_CmsButton.ButtonColor;
import org.opencms.gwt.client.ui.I_CmsButton.ButtonStyle;
import org.opencms.gwt.client.ui.css.I_CmsInputLayoutBundle;
import org.opencms.gwt.client.ui.input.CmsLabel;
import org.opencms.gwt.client.ui.input.CmsTextBox;
import org.opencms.gwt.client.ui.input.form.CmsFieldsetFormFieldPanel;
import org.opencms.gwt.shared.CmsListInfoBean;
import org.opencms.util.CmsStringUtil;
import org.opencms.util.CmsUUID;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The create new gallery folder dialog.<p>
 */
public class CmsCreateGalleryDialog extends CmsPopup {

    /** The dialog width. */
    private static final int DIALOG_WIDTH = 530;

    /** The text metrics key. */
    private static final String METRICS_KEY = "CREATE_NEW_GALLERY_DIALOG";

    /** The controller. */
    private CmsSitemapController m_controller;

    /** The dialog content panel. */
    private CmsFieldsetFormFieldPanel m_dialogContent;

    /** The folder name input. */
    private CmsTextBox m_folderNameInput;

    /** The OK button. */
    private CmsPushButton m_okButton;

    /** The parent folder id. */
    private CmsUUID m_parentId;

    /** The resource type id. */
    private int m_resourceTypeId;

    /** The title input. */
    private CmsTextBox m_titleInput;

    /** 
     * Constructor.<p>
     * 
     * @param controller the controller
     * @param resourceTypeId the gallery type id
     * @param parentId the parent folder id
     */
    public CmsCreateGalleryDialog(CmsSitemapController controller, int resourceTypeId, CmsUUID parentId) {

        super(org.opencms.ade.sitemap.client.Messages.get().key(
            org.opencms.ade.sitemap.client.Messages.GUI_GALLERIES_CREATE_0), DIALOG_WIDTH);
        m_resourceTypeId = resourceTypeId;
        m_parentId = parentId;
        m_controller = controller;
        CmsGalleryType type = m_controller.getGalleryType(new Integer(resourceTypeId));
        CmsListInfoBean listInfo = new CmsListInfoBean(type.getNiceName(), type.getDescription(), null);
        listInfo.setResourceType(type.getTypeName());
        m_dialogContent = new CmsFieldsetFormFieldPanel(listInfo, null);
        m_dialogContent.addStyleName(I_CmsInputLayoutBundle.INSTANCE.inputCss().highTextBoxes());
        m_dialogContent.getFieldSet().setOpenerVisible(false);
        m_dialogContent.getFieldSet().getElement().getStyle().setMarginTop(4, Style.Unit.PX);
        setMainContent(m_dialogContent);
        m_folderNameInput = new CmsTextBox();
        m_folderNameInput.setTriggerChangeOnKeyPress(true);
        m_folderNameInput.addValueChangeHandler(new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {

                setOkEnabled(CmsStringUtil.isNotEmptyOrWhitespaceOnly(event.getValue()));
            }
        });
        addInputRow(
            org.opencms.ade.sitemap.client.Messages.get().key(
                org.opencms.ade.sitemap.client.Messages.GUI_GALLERIES_LABEL_FOLDER_NAME_0),
            m_folderNameInput);
        m_titleInput = new CmsTextBox();
        addInputRow(
            org.opencms.ade.sitemap.client.Messages.get().key(
                org.opencms.ade.sitemap.client.Messages.GUI_GALLERIES_LABEL_TITLE_0),
            m_titleInput);
        this.addDialogClose(null);

        CmsPushButton closeButton = new CmsPushButton();
        closeButton.setText(Messages.get().key(Messages.GUI_CANCEL_0));
        closeButton.setUseMinWidth(true);
        closeButton.setButtonStyle(ButtonStyle.TEXT, ButtonColor.BLUE);
        closeButton.addClickHandler(new ClickHandler() {

            /**
             * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
             */
            public void onClick(ClickEvent event) {

                hide();
            }
        });
        addButton(closeButton);

        m_okButton = new CmsPushButton();
        m_okButton.setText(Messages.get().key(Messages.GUI_OK_0));
        m_okButton.setUseMinWidth(true);
        m_okButton.setButtonStyle(ButtonStyle.TEXT, ButtonColor.RED);
        m_okButton.addClickHandler(new ClickHandler() {

            /**
             * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
             */
            public void onClick(ClickEvent event) {

                onOk();
            }
        });
        addButton(m_okButton);
        setOkEnabled(false);
        m_dialogContent.truncate(METRICS_KEY, DIALOG_WIDTH - 20);
    }

    /**
     * Creates the new gallery folder.<p>
     */
    protected void onOk() {

        m_controller.createNewGallery(
            m_parentId,
            m_resourceTypeId,
            m_folderNameInput.getFormValueAsString(),
            m_titleInput.getFormValueAsString());
        hide();
    }

    /**
     * Enables or disables the OK button.<p>
     * 
     * @param enabled <code>true</code> to enable the button
     */
    protected void setOkEnabled(boolean enabled) {

        if (enabled) {
            m_okButton.enable();
        } else {
            m_okButton.disable(org.opencms.ade.sitemap.client.Messages.get().key(
                org.opencms.ade.sitemap.client.Messages.GUI_NEW_GALLERY_VALIDATION_0));
        }
    }

    /**
     * Adds a row to the form.<p>
     * 
     * @param label the label
     * @param inputWidget the input widget
     */
    private void addInputRow(String label, Widget inputWidget) {

        FlowPanel row = new FlowPanel();
        row.setStyleName(I_CmsSitemapLayoutBundle.INSTANCE.sitemapCss().inputRow());
        CmsLabel labelWidget = new CmsLabel(label);
        labelWidget.setStyleName(I_CmsSitemapLayoutBundle.INSTANCE.sitemapCss().inputLabel());
        row.add(labelWidget);
        inputWidget.addStyleName(I_CmsSitemapLayoutBundle.INSTANCE.sitemapCss().inputBox());
        row.add(inputWidget);
        m_dialogContent.getFieldSet().add(row);
    }

}
