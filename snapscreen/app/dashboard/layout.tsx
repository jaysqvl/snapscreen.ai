"use client"

import { AppNavbar } from "@/components/app-navbar"

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <div className="flex min-h-screen w-full flex-col">
      <AppNavbar />
      {children}
    </div>
  )
} 