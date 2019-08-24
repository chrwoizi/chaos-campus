namespace Javy
{
    partial class ResizeMapForm
    {
        /// <summary>
        /// Erforderliche Designervariable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Verwendete Ressourcen bereinigen.
        /// </summary>
        /// <param name="disposing">True, wenn verwaltete Ressourcen gelöscht werden sollen; andernfalls False.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Vom Windows Form-Designer generierter Code

        /// <summary>
        /// Erforderliche Methode für die Designerunterstützung.
        /// Der Inhalt der Methode darf nicht mit dem Code-Editor geändert werden.
        /// </summary>
        private void InitializeComponent()
        {
            this.okayButton = new System.Windows.Forms.Button();
            this.mapSize = new System.Windows.Forms.GroupBox();
            this.label3 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label1 = new System.Windows.Forms.Label();
            this.unitCombo = new System.Windows.Forms.ComboBox();
            this.widthField = new System.Windows.Forms.NumericUpDown();
            this.heightField = new System.Windows.Forms.NumericUpDown();
            this.button1 = new System.Windows.Forms.Button();
            this.mapSize.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.widthField)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.heightField)).BeginInit();
            this.SuspendLayout();
            // 
            // okayButton
            // 
            this.okayButton.DialogResult = System.Windows.Forms.DialogResult.OK;
            this.okayButton.Location = new System.Drawing.Point(37, 124);
            this.okayButton.Name = "okayButton";
            this.okayButton.Size = new System.Drawing.Size(54, 21);
            this.okayButton.TabIndex = 2;
            this.okayButton.Text = "OK";
            this.okayButton.UseVisualStyleBackColor = true;
            this.okayButton.Click += new System.EventHandler(this.okayButton_Click);
            // 
            // mapSize
            // 
            this.mapSize.Controls.Add(this.label3);
            this.mapSize.Controls.Add(this.label2);
            this.mapSize.Controls.Add(this.label1);
            this.mapSize.Controls.Add(this.unitCombo);
            this.mapSize.Controls.Add(this.widthField);
            this.mapSize.Controls.Add(this.heightField);
            this.mapSize.Location = new System.Drawing.Point(12, 12);
            this.mapSize.Name = "mapSize";
            this.mapSize.Size = new System.Drawing.Size(184, 106);
            this.mapSize.TabIndex = 9;
            this.mapSize.TabStop = false;
            this.mapSize.Text = "Map Size";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(6, 74);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(38, 13);
            this.label3.TabIndex = 7;
            this.label3.Text = "Width:";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(6, 48);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(41, 13);
            this.label2.TabIndex = 6;
            this.label2.Text = "Height:";
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(6, 22);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(29, 13);
            this.label1.TabIndex = 5;
            this.label1.Text = "Unit:";
            // 
            // unitCombo
            // 
            this.unitCombo.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.unitCombo.FormattingEnabled = true;
            this.unitCombo.Items.AddRange(new object[] {
            "Blocks",
            "Fields"});
            this.unitCombo.Location = new System.Drawing.Point(60, 19);
            this.unitCombo.Name = "unitCombo";
            this.unitCombo.Size = new System.Drawing.Size(85, 21);
            this.unitCombo.TabIndex = 4;
            this.unitCombo.SelectedIndexChanged += new System.EventHandler(this.unitCombo_SelectedIndexChanged);
            // 
            // widthField
            // 
            this.widthField.Location = new System.Drawing.Point(60, 72);
            this.widthField.Maximum = new decimal(new int[] {
            1000,
            0,
            0,
            0});
            this.widthField.Minimum = new decimal(new int[] {
            8,
            0,
            0,
            0});
            this.widthField.Name = "widthField";
            this.widthField.Size = new System.Drawing.Size(85, 20);
            this.widthField.TabIndex = 3;
            this.widthField.Value = new decimal(new int[] {
            20,
            0,
            0,
            0});
            this.widthField.ValueChanged += new System.EventHandler(this.widthField_ValueChanged);
            // 
            // heightField
            // 
            this.heightField.Location = new System.Drawing.Point(60, 46);
            this.heightField.Maximum = new decimal(new int[] {
            1000,
            0,
            0,
            0});
            this.heightField.Minimum = new decimal(new int[] {
            8,
            0,
            0,
            0});
            this.heightField.Name = "heightField";
            this.heightField.Size = new System.Drawing.Size(85, 20);
            this.heightField.TabIndex = 2;
            this.heightField.Value = new decimal(new int[] {
            20,
            0,
            0,
            0});
            this.heightField.ValueChanged += new System.EventHandler(this.heightField_ValueChanged);
            // 
            // button1
            // 
            this.button1.DialogResult = System.Windows.Forms.DialogResult.Cancel;
            this.button1.Location = new System.Drawing.Point(116, 124);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(66, 21);
            this.button1.TabIndex = 10;
            this.button1.Text = "Cancel";
            this.button1.UseVisualStyleBackColor = true;
            // 
            // ResizeMapForm
            // 
            this.AcceptButton = this.okayButton;
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.CancelButton = this.button1;
            this.ClientSize = new System.Drawing.Size(205, 152);
            this.Controls.Add(this.button1);
            this.Controls.Add(this.mapSize);
            this.Controls.Add(this.okayButton);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "ResizeMapForm";
            this.ShowIcon = false;
            this.ShowInTaskbar = false;
            this.Text = "ResizeMapForm";
            this.Load += new System.EventHandler(this.ResizeMapForm_Load);
            this.mapSize.ResumeLayout(false);
            this.mapSize.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.widthField)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.heightField)).EndInit();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Button okayButton;
        private System.Windows.Forms.GroupBox mapSize;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.ComboBox unitCombo;
        private System.Windows.Forms.NumericUpDown widthField;
        private System.Windows.Forms.NumericUpDown heightField;
        private System.Windows.Forms.Button button1;
    }
}